from fastapi import FastAPI, HTTPException, Depends, UploadFile, File, Request, Form
from fastapi.responses import FileResponse
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
from starlette.formparsers import MultiPartParser
from starlette.middleware.base import BaseHTTPMiddleware
import os
import json
from pathlib import Path
import uuid


# Database connection
SQLALCHEMY_DATABASE_URL = "oracle://C##sivertab2:senha@localhost:1521/xe"
engine = create_engine(SQLALCHEMY_DATABASE_URL)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()
BASE_PATH = r'C:/Users/felip/byd/imgs'
TESTE_PATH = 'delearX/Carro 3/Pdi Y'
ALLOWED_IMAGE_EXTENSIONS = {
    'jpg', 'jpeg', 'png',
    }

# MAX_BODY_SIZE  = 10 * 1024 * 1024

# class LimitUploadSizeMiddleware(BaseHTTPMiddleware):
#     async def dispatch(self, request: Request, call_next):
#         body = await request.body()
#         if len(body) > MAX_BODY_SIZE:
#             raise HTTPException(status_code=413, detail="Upload excede o tamanho máximo permitido")
#         return await call_next(request)



# =================================
# Database and pydantic Models (DTO)
# =================================

#Database Model for pdi images info
class ImageInfoPDI(Base):
    __tablename__ = "image_info_pdi"

    img_id = Column(Integer, primary_key=True)
    img_path = Column(String, nullable=False)
    uploaded_by_user_id = Column(String)
    upload_date = Column(DateTime, default=datetime.now())
    pdi_id = Column(Integer)

#pydantic Model for pdi images to use in the api
class ImageInfoPDIBase(BaseModel):
    #img_id: int (deve ser gerado automaticamente)
    # img_path: str (Ver esse aqui como será passado automaticamente)
    uploaded_by_user_id: str
    #upload_date: datetime (deve ser gerado automaticamente)
    pdi_id: int

    class Config:
        from_attributes = True

class ImageInfoPDIResponse(ImageInfoPDIBase):
    #car_id: int  # agora o campo 'id' é obrigatório na resposta

    class Config:
        from_attributes = True


#Database model for pdi images classification
class PDI_image(Base):
    __tablename__ = "pdi_image"

    pdi_id = Column(Integer)
    image_id = Column(Integer, primary_key=True)
    image_type = Column(Integer)

#pydantic model for pdi images classification
class PDI_imageBase(BaseModel):
    pdi_id: int
    image_id: int
    image_type: int

    class Config:
        from_attributes = True


#
class PDI_Image_type(Base):
    __tablename__ = "pdi_image_type"

    type_id = Column(Integer, primary_key=True)
    type_name = Column(String, nullable=False)


class PDI_Image_typeBase(BaseModel):
    type_id: int
    type_name: str


    
def allowed_file(filename: str) -> bool:
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_IMAGE_EXTENSIONS

   
# =================
# App and endpoints
# =================

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()



###Function to download the image in the right
types = {1: "Chassi1", 
        2: "Chassi2",
        3: "Chassi3", 
        4: "Chassi4", 
        5: "SOC1",
        6: "SOC2",
        7: "SOC3",
        8: "SOC4",
        9: "Pneu1",
        10: "Pneu2",
        11: "Pneu3",
        12: "Pneu4",
        13: "Bateria12v1",
        14: "Bateria12v2",
        15: "Bateria12v3",
        16: "Bateria12v4",
        17: "Extra", 
        18: "Extra 2"}

def store_img(file, base_path, dealer_code, vin_car, pdi_id, img_type_id):
    img_type = types[img_type_id]
    pdi_dir = Path(base_path) / dealer_code / vin_car / f"Pdi {pdi_id}" / img_type
    pdi_dir.mkdir(parents=True, exist_ok=True)

    extension = Path(file.filename).suffix or ""
    unique_file_name = f'{uuid.uuid4()}{extension}'
    img_path = pdi_dir / unique_file_name
    return img_path
    


# FastAPI app
app = FastAPI(title="BYD PDI API")
#app.add_middleware(LimitUploadSizeMiddleware)


#Get all the images from a pdi, adn generates a endpoint for each image 
@app.get("/image_info/{pdi_id}")
async def get_pdi_images(pdi_id: int, request: Request, db: Session = Depends(get_db)):
    images = db.query(ImageInfoPDI).filter(ImageInfoPDI.pdi_id == pdi_id).all()
    if not images:
        raise HTTPException(status_code=404, detail="Imagem não encontrada")
    base_url = str(request.base_url)
    urls = [f"{base_url}image/{image.img_id}" for image in images]
    return {"urls": urls}


#Get image from image id in the created endpoint
@app.get("/image/{img_id}")
async def get_image(img_id: int, db: Session = Depends(get_db)):
    image_record = db.query(ImageInfoPDI).filter(ImageInfoPDI.img_id == img_id).first()
    if not image_record:
        raise HTTPException(status_code=404, detail="Imagem não encontrada")
    return FileResponse(image_record.img_path, media_type="image/jpeg")


#Get image type from image id
@app.get("/image_type/{img_id}")
async def get_image_type(img_id: int, db: Session = Depends(get_db)):
    image_record = db.query(PDI_image).filter(PDI_image.image_id == img_id).first()
    if not image_record:
        raise HTTPException(status_code=404, detail="Imagem não encontrada")
    return image_record.image_type


@app.post("/image_type/")
async def post_image_type():
    pass


#Post of images
@app.post ("/upload_image/")
async def upload_image(vin_car: str = Form(...), 
                       dealer_code: str = Form(...), 
                       img_type_id: int = Form(...),
                       data: str = Form(...), 
                       file: UploadFile = File(...), 
                       db: Session = Depends(get_db)):
    if not allowed_file(file.filename):
        raise HTTPException(status_code=400, detail="Tipo de arquivo não permitido")
    data = json.loads(data)
    img_model = ImageInfoPDIResponse(**data)
    
    img_path = store_img(file, BASE_PATH, dealer_code, vin_car, img_model.pdi_id, img_type_id)
    print(img_path)
    save_dir = os.path.dirname(img_path)
    img_path = str(img_path)
    
    os.makedirs(os.path.dirname(img_path), exist_ok=True)#ver melhor isso dps
    try:
        with open(img_path, "wb") as buffer:
            buffer.write(await file.read())
            print("salvou")
        
        print(data)
        #new_img = ImageInfoPDI(data, uplaod_date = datetime.now(), img_path = img_path)
        new_img = ImageInfoPDI(upload_date = datetime.now(), 
                               img_path = img_path, pdi_id = img_model.pdi_id, 
                               uploaded_by_user_id = img_model.uploaded_by_user_id,
                               )
        db.add(new_img)
    except Exception as e:
        raise e
    
    if new_img:
        try:
            db.commit()
            db.refresh(new_img)
        except Exception as e:
            db.rollback()
            raise HTTPException(status_code=401, detail=str(e))
    

    return {"message": f"{img_path} saved at {save_dir}",
             "new_img": new_img}
    




if __name__ == "__main__":
    import uvicorn

    uvicorn.run(app, host="0.0.0.0", port=5000)
