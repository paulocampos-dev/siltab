from sqlalchemy import (
    Column,
    Integer,
    String, 
    ForeignKey,
    DateTime,   
)

from NewBackend.database.connection import Base

class Car_to_PDI(Base):
    __tablename__ = "car_to_pdi"

    car_id = Column(Integer, ForeignKey("car_info.car_id"), primary_key=True)
    pdi_id = Column(Integer, ForeignKey("pdi_info.pdi_id"), primary_key=True)
    create_date = Column(DateTime, nullable=False,) #default=datetime)
