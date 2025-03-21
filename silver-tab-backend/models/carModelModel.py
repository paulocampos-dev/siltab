from sqlalchemy import (
    Column,
    Integer,
    String,
)
from database.connection import Base


class CarModel(Base):
    __tablename__ = "car_model"

    car_model_id = Column(Integer, primary_key=True, autoincrement=True)
    car_model_name = Column(String(100), nullable=False)

