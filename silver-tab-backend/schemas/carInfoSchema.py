from typing import List, Optional, Any
from pydantic import BaseModel
from datetime import datetime


class CarsBase(BaseModel):
    car_id: int
    vin: str
    dealer_code: Optional[str] = None
    car_model_id: int
    is_sold: Optional[bool] = False
    sold_date: Optional[datetime] = None

    class Config:
        from_attributes = True


class CarsUpload(BaseModel):
    sold_date: datetime


class CarsPost(BaseModel):
    # car_id: int
    vin: str
    dealer_code: Optional[str] = None
    car_model_id: int
    is_sold: Optional[bool] = False
    sold_date: Optional[datetime] = None

    class Config:
        from_attributes = True


class CarFullResponseForKotlin(BaseModel):
    car_id: int
    vin: str
    dealer_code: Optional[str] = None
    car_model_name: str
    is_sold: Optional[bool] = False
    sold_date: Optional[datetime] = None
    Pdis: Optional[List[int]] = None

    class Config:
        from_attributes = True


class CarNewVin(BaseModel):
    vin: str

    class Config:
        from_attributes = True
