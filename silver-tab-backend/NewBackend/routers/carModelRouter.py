from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from typing import List
from database.connection import get_db
from models.carModelModel import CarModel
from schemas.carModelSchema import CarModelBase, CarModelResponse, CarFullResponseForKotlin



router = APIRouter(
    prefix="/car-models",
    tags=["Car Models"]
)

@router.get("/", response_model=List[CarModelResponse])
def get_all_car_models(db: Session = Depends(get_db)):
    return db.query(CarModel).all()


@router.get("/{car_model_id}", response_model=CarModelResponse)
def get_car_model_by_id(car_model_id: int, db: Session = Depends(get_db)):
    car_model = db.query(CarModel).filter(CarModel.car_model_id == car_model_id).first()
    if not car_model:
        raise HTTPException(status_code=404, detail="Car model not found")
    return car_model