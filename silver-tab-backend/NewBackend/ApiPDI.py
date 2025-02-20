from fastapi import FastAPI, HTTPException, Depends, Response, status
from sqlalchemy import (
    create_engine,
    Column,
    Integer,
    String,
    SmallInteger,
    Boolean,
    ForeignKey,
    DateTime,
    Text,
)
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker, Session
from datetime import datetime
from typing import List, Optional, Any
from pydantic import BaseModel
import uuid
from sqlalchemy.types import TypeDecorator, CLOB
from sqlalchemy.dialects.oracle import RAW
from sqlalchemy import Sequence
from sqlalchemy import Float
import json




# Database connection
SQLALCHEMY_DATABASE_URL = "oracle://c##sivertab2:senha@localhost:1521/xe"
engine = create_engine(SQLALCHEMY_DATABASE_URL)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()

class JSON (TypeDecorator):
    impl = CLOB

    def process_bind_param(self, value, dialect):
        if value is not None:
            value = json.dumps(value)
        return value

    def process_result_value(self, value, dialect):
        if value is not None:
            value = json.loads(value)
        return value


# Custom UUID Type for Oracle
# class UUIDType(TypeDecorator):
#     impl = RAW(16)
#     cache_ok = True

#     def process_bind_param(self, value, dialect):
#         if value is None:
#             return value
#         elif dialect.name == "oracle":
#             return uuid.UUID(value).bytes
#         return value

#     def process_result_value(self, value, dialect):
#         if value is None:
#             return value
#         if dialect.name == "oracle":
#             return str(uuid.UUID(bytes=value))
#         return value


# Database Models
class Cars(Base):
    __tablename__ = "cars"

    car_id = Column(Integer, primary_key=True)
    model = Column(String, nullable=False)
    dealer_code = Column(String)
    chassi_number = Column(String, nullable=False, unique=True)
    pdi_ids = Column(JSON, nullable=True)

#pydantic for api request/response

class CarsBase(BaseModel):
    #car_id: Optional[int] = None
    model: str
    dealer_code: Optional[str] = None
    chassi_number: str
    pdi_ids: Optional[Any] = None  # using your custom type

    model_config = {
        "from_attributes": True,
        "arbitrary_types_allowed": True,
    }


# class UserBYD(Base):
#     __tablename__ = "users_byd"

#     id = Column(Integer, primary_key=True)


class PDI(Base):
    __tablename__ = "pdi_link_to_dealer_cars"

    pdi_id = Column(Integer, primary_key=True, nullable=False)
    car_id = Column(Integer, ForeignKey("cars.car_id"), nullable=False)
    user_id = Column(Integer, nullable=False)
    dealer_code = Column(String, nullable=False)
    created_at = Column(DateTime, nullable=False)
    chassi_number = Column(String)
    soc_percentage = Column('SOC_PERCENTAGE', Float)
    battery12v = Column('BATTERYV12', Integer)
    five_minutes_hybrid = Column('FIVE_MINUTES_HYBRID', Boolean)
    tire_pressure_dd = Column('TIRE_PRESSURE_DD', Float)
    tire_pressure_de = Column('TIRE_PRESSURE_DE', Float)
    tire_pressure_td = Column('TIRE_PRESSURE_TD', Float)
    tire_pressure_te = Column('TIRE_PRESSURE_TE', Float)
    extra_text = Column('EXTRA_TEXT', Text)


# Pydantic models for request/response
class PDIBase(BaseModel):
    #pdi_id: int
    car_id: int
    user_id: int
    dealer_code: str
    #created_at: datetime
    chassi_number: Optional[str] = None
    chassi_image_id: Optional[int] = None
    soc_percentage: Optional[float] = None
    soc_image_id: Optional[int] = None
    battery12v: Optional[int] = None
    battery12v_image_id: Optional[int] = None
    five_minutes_hybrid: Optional[bool] = None
    tire_pressure_dd: Optional[float] = None
    tire_pressure_de: Optional[float] = None
    tire_pressure_td: Optional[float] = None
    tire_pressure_te: Optional[float] = None
    tire_pressure_image_id: Optional[int] = None
    extra_image_id: Optional[int] = None
    extra2_image_id: Optional[int] = None
    extra_text: Optional[str] = None

    class Config:
        from_attributes = True


class CarsResponse(CarsBase):
    #car_id: int  # agora o campo 'id' é obrigatório na resposta

    class Config:
        from_attributes = True

class PDICreate(PDIBase):
    pass


class PDIUpdate(PDIBase):
    pass


class PDIResponse(PDIBase):
    # Here, `pdi_id` becomes non-optional, assuming that when you 
    # return a PDI, you always have a valid `pdi_id`.
    pdi_id: int

    class Config:
        from_attributes = True




# Dependency
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()


# FastAPI app
app = FastAPI(title="BYD PDI API")

# Get all cars
@app.get("/cars/", response_model=List[CarsResponse])
def get_all_cars(db: Session = Depends(get_db)):
    """Lista todos os veículos cadastrados"""
    return db.query(Cars).all()

# Get single car by chassi
@app.get("/cars/{chassi_number}", response_model=CarsResponse)
def get_car(chassi_number: str, db: Session = Depends(get_db)):
    
    car = db.query(Cars).filter(Cars.chassi_number == chassi_number).first()
    
    if not car:
        raise HTTPException(status_code=404, detail="Veículo não encontrado")
        
    return car

#get car by dealer
@app.get("/cars/dealer/{dealer_code}", response_model=List[CarsResponse])
def get_dealer_cars(dealer_code: str, db: Session = Depends(get_db)):
    """Get all cars from a specific dealer"""
    dealer = db.query(Cars).filter(Cars.dealer_code == dealer_code).first()
    if not dealer:
        raise HTTPException(status_code=405, detail="dealer code not found")

    cars = db.query(Cars).filter(Cars.dealer_code == dealer_code).all()
    return cars

@app.post("/cars/", response_model=CarsResponse)
def create_car(cars: CarsResponse, db: Session = Depends(get_db)):
    new_car = Cars(**cars.dict())
    db.add(new_car)
    db.commit()
    db.refresh(new_car)
    return new_car

@app.delete("/cars/{car_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_car(car_id: int, db: Session = Depends(get_db)):
    
    car = db.query(Cars).filter(Cars.car_id == car_id).first()
    if not car:
        raise HTTPException(status_code=404, detail="Veículo não encontrado")
    
    db.delete(car)
    db.commit()
    return Response(status_code=status.HTTP_204_NO_CONTENT)



# ----------------------------
# Endpoints para os PDI
# ----------------------------

#all pdis
@app.get("/pdi/", response_model=List[PDIResponse])
def get_all_pdis(db: Session = Depends(get_db)):
    """Lista todos os registros de PDI"""
    return db.query(PDI).all()


#pdis for a specific dealer
@app.get("/pdi/dealer/{dealer_code}", response_model=List[PDIResponse])
async def get_dealer_pdis(dealer_code: str, db: Session = Depends(get_db)):
    """Get all PDIs performed by a specific dealer"""
    dealer = db.query(PDI).filter(PDI.dealer_code == dealer_code).first()
    if not dealer:
        raise HTTPException(status_code=405, detail="dealer code not found")

    pdis = db.query(PDI).filter(PDI.dealer_code == dealer_code).all()
    return pdis


@app.post("/pdi/", response_model=PDIResponse)
async def create_pdi(pdi: PDICreate, db: Session = Depends(get_db)):
    """Create a new PDI record"""
    
    #Adicionar um post para carro inexistente que irá criar o uuid automaticamente

    db_pdi = PDI(**pdi.dict(), created_at=datetime.now())
    db.add(db_pdi)
    try:
        db.commit()
        db.refresh(db_pdi)
    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=401, detail=str(e))

    return db_pdi


@app.put("/pdi/{pdi_id}", response_model=PDIResponse)
async def update_pdi(pdi_id: int, pdi_update: PDIUpdate, db: Session = Depends(get_db)):
    """Update an existing PDI record"""
    db_pdi = db.query(PDI).filter(PDI.pdi_id == pdi_id).first()
    if not db_pdi:
        raise HTTPException(status_code=404, detail="PDI not found")

    # Update PDI fields
    for field, value in pdi_update.dict(exclude_unset=True).items():
        setattr(db_pdi, field, value)

    try:
        db.commit()
        db.refresh(db_pdi)
    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=400, detail=str(e))

    return db_pdi


if __name__ == "__main__":
    import uvicorn

    uvicorn.run(app, host="0.0.0.0", port=8000)
