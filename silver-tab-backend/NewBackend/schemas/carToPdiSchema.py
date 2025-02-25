from typing import List, Optional, Any
from datetime import datetime
from pydantic import BaseModel




# car_id = Column(Integer, ForeignKey("car_info.car_id"), primary_key=True)
#     pdi_id = Column(Integer, ForeignKey("pdi_info.pdi_id"), primary_key=True)
#     create_date = Column(DateTime, nullable=False,) #default=datetime)



class carToPdiBase (BaseModel):
    
    car_id: int #
    pdi_id: int
    create_date: datetime

    class Config:
        from_attributes = True




