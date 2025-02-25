from sqlalchemy import (
    Column,
    Integer,
    String,  
    Boolean ,
    DateTime,
)

from NewBackend.database.connection import Base


class Cars(Base):
    __tablename__ = "car_info"

    car_id = Column(Integer, primary_key=True)
    chassi_number = Column(String, nullable=False, unique=True)
    dealer_code = Column(String)
    car_model_id  = Column(Integer, nullable=False)
    is_sold = Column(Boolean, default=False) 
    sold_date = Column(DateTime)