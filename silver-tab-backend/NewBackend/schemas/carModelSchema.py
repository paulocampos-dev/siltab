
from pydantic import BaseModel
from typing import Optional

class CarModelBase(BaseModel):
    car_model_name: str

    class Config:
        from_attributes = True

class CarModelResponse(CarModelBase):
    car_model_id: Optional[int]   #Será que vai precisar