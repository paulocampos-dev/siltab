from fastapi import FastAPI, HTTPException, Depends
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
from typing import List, Optional
from pydantic import BaseModel
import uuid
from sqlalchemy.types import TypeDecorator
from sqlalchemy.dialects.oracle import RAW
from sqlalchemy import Sequence

# Database connection
SQLALCHEMY_DATABASE_URL = "oracle://c##silver_tab:silvertab@localhost:1521/xe"
engine = create_engine(SQLALCHEMY_DATABASE_URL)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()


# Custom UUID Type for Oracle
class UUIDType(TypeDecorator):
    impl = RAW(16)
    cache_ok = True

    def process_bind_param(self, value, dialect):
        if value is None:
            return value
        elif dialect.name == "oracle":
            return uuid.UUID(value).bytes
        return value

    def process_result_value(self, value, dialect):
        if value is None:
            return value
        if dialect.name == "oracle":
            return str(uuid.UUID(bytes=value))
        return value


# Database Models
class Car(Base):
    __tablename__ = "cars"

    id = Column(UUIDType, primary_key=True, default=lambda: str(uuid.uuid4()))
    model = Column(String, nullable=False)
    year = Column(SmallInteger, nullable=False)
    vin = Column(String, nullable=False, unique=True)


class UserBYD(Base):
    __tablename__ = "users_byd"

    id = Column(Integer, primary_key=True)


class PDI(Base):
    __tablename__ = "pdi"

    id = Column(Integer, Sequence('pdi_id_seq'), primary_key=True)
    car_id = Column(UUIDType, ForeignKey("cars.id"))
    inspector_id = Column(Integer, ForeignKey("users_byd.id"))
    inspection_date = Column(DateTime, nullable=False)
    chassi_number = Column(Integer)
    chassi_image_path = Column(String)
    soc_percentage = Column(SmallInteger)
    soc_percentage_image_path = Column(String)
    battery_12v = Column("BATTERY_12V", SmallInteger)
    battery_12v_image_path = Column("BATTERY_12V_IMAGE_PATH", String)
    tire_pressure_dd = Column("TIRE_PRESSURE_DD", SmallInteger)
    tire_pressure_de = Column("TIRE_PRESSURE_DE", SmallInteger)
    tire_pressure_td = Column("TIRE_PRESSURE_TD", SmallInteger)
    tire_pressure_te = Column("TIRE_PRESSURE_TE", SmallInteger)
    tire_pressure_image_path = Column(String)
    five_minutes_hybrid = Column(Boolean)
    extra_text = Column(Text)
    extra_image_1 = Column(String)
    extra_image_2 = Column(String)
    extra_image_3 = Column(String)


# Pydantic models for request/response
class PDIBase(BaseModel):
    car_id: str
    inspector_id: int
    chassi_number: Optional[int] = None
    chassi_image_path: Optional[str] = None
    soc_percentage: Optional[int] = None
    soc_percentage_image_path: Optional[str] = None
    battery_12v: Optional[int] = None
    battery_12v_image_path: Optional[str] = None
    tire_pressure_dd: Optional[int] = None
    tire_pressure_de: Optional[int] = None
    tire_pressure_td: Optional[int] = None
    tire_pressure_te: Optional[int] = None
    tire_pressure_image_path: Optional[str] = None
    five_minutes_hybrid: Optional[bool] = None
    extra_text: Optional[str] = None
    extra_image_1: Optional[str] = None
    extra_image_2: Optional[str] = None
    extra_image_3: Optional[str] = None

class CarsBase(BaseModel):
    id: str
    model: str
    year: int
    vin: str


class CarsResponse(CarsBase):
    class Config:
        from_attributes = True
        json_encoders = {uuid.UUID: str}

class PDICreate(PDIBase):
    pass


class PDIUpdate(PDIBase):
    pass


class PDIResponse(PDIBase):
    id: int
    inspection_date: datetime

    class Config:
        from_attributes = True
        json_encoders = {uuid.UUID: str}




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
    return db.query(Car).all()

# Get single car by ID
@app.get("/cars/{car_id}", response_model=CarsResponse)
def get_car(car_id: str, db: Session = Depends(get_db)):
    """Busca um veículo específico pelo UUID"""
    try:
        # Validação do formato do UUID
        uuid.UUID(car_id)  
    except ValueError:
        raise HTTPException(status_code=400, detail="ID inválido")
    
    car = db.query(Car).filter(Car.id == car_id).first()
    
    if not car:
        raise HTTPException(status_code=404, detail="Veículo não encontrado")
        
    return car

@app.get("/pdi/cars/", response_model=List[PDIResponse])
async def get_inspector_pdis(inspector_id: int, db: Session = Depends(get_db)):
    """Get all PDIs performed by a specific inspector"""
    inspector = db.query(UserBYD).filter(UserBYD.id == inspector_id).first()
    if not inspector:
        raise HTTPException(status_code=405, detail="Inspector not found")

    pdis = db.query(PDI).filter(PDI.inspector_id == inspector_id).all()
    return pdis


@app.get("/pdi/inspector/{inspector_id}", response_model=List[PDIResponse])
async def get_inspector_pdis(inspector_id: int, db: Session = Depends(get_db)):
    """Get all PDIs performed by a specific inspector"""
    inspector = db.query(UserBYD).filter(UserBYD.id == inspector_id).first()
    if not inspector:
        raise HTTPException(status_code=405, detail="Inspector not found")

    pdis = db.query(PDI).filter(PDI.inspector_id == inspector_id).all()
    return pdis


@app.post("/pdi/", response_model=PDIResponse)
async def create_pdi(pdi: PDICreate, db: Session = Depends(get_db)):
    """Create a new PDI record"""
    # Validate and convert car_id to UUID string
    try:
        car_uuid = str(uuid.UUID(pdi.car_id))
    except ValueError:
        raise HTTPException(status_code=401, detail="Invalid car_id format")

    #Adicionar um post para carro inexistente que irá criar o uuid automaticamente


    # Verify car exists
    car = db.query(Car).filter(Car.id == car_uuid).first()
    if not car:
        raise HTTPException(status_code=405, detail="Car not found")

    # Verify inspector exists
    # inspector = db.query(UserBYD).filter(UserBYD.id == pdi.inspector_id).first()
    # if not inspector:
    #     raise HTTPException(status_code=405, detail="Inspector not found")

    db_pdi = PDI(**pdi.dict(), inspection_date=datetime.now())
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
    db_pdi = db.query(PDI).filter(PDI.id == pdi_id).first()
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
