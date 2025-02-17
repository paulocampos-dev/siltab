from fastapi import FastAPI, HTTPException, Depends, UploadFile, File, Request
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




# Database connection
SQLALCHEMY_DATABASE_URL = "oracle://c##silver_tab:silvertab@localhost:1521/xe"
engine = create_engine(SQLALCHEMY_DATABASE_URL)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()
BASE_SAVE_IMAGE_URL = r'C:\Users\felip\byd'
TESTE_PATH = r'delearX\Carro 3\Pdi Y'


MultiPartParser.max_file_size = 10 * 1024 * 1024


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
    img_id: int
    img_path: str
    uploaded_by_user_id: str
    upload_date: datetime
    pdi_id: int

    class Config:
        from_attributes = True


#Database model for pdi images classification
class PDI_image(Base):
    __tablename__ = "pdi_image"

    pdi_id = Column(Integer)
    image_id = Column(Integer)
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


    

# =================
# App and endpoints
# =================

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()


# FastAPI app
app = FastAPI(title="BYD PDI API")


#Get all the images from a pdi, adn generates a endpoint for each image 
@app.get("/image_info/{pdi_id}")
async def get_pdi_images(pdi_id: int, request: Request, db: Session = Depends(get_db)):
    images = db.query(ImageInfoPDI).filter(ImageInfoPDI.pdi_id == pdi_id).all()
    if not images:
        raise HTTPException(status_code=404, detail="Imagem não encontrada")
    base_url = str(request.base_url)
    urls = [f"{base_url}images/{image.img_id}" for image in images]
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
async def get_image_tipe(img_id: int, db: Session = Depends(get_db)):
    image_record = db.query(PDI_image).filter(PDI_image.image_id == img_id).first()
    if not image_record:
        raise HTTPException(status_code=404, detail="Imagem não encontrada")
    return image_record.image_type



#Post of images
@app.post ("/upload_image/")
async def upload_image(file: UploadFile = File(...), pdi_id: int = 0, db: Session = Depends(get_db)):
    img_path = f"{BASE_SAVE_IMAGE_URL}/{TESTE_PATH}/{file.filename}"    #arrumar para seguir todo o caminho certo
    



if __name__ == "__main__":
    import uvicorn

    uvicorn.run(app, host="0.0.0.0", port=5000)
