from typing import List, Optional, Any
from pydantic import BaseModel
from datetime import datetime



@property
def five_minutes_hybrid_boolean(self) -> bool:
    return False if self.five_minutes_hybrid == 0 else True

class PDIBase(BaseModel):
    #pdi_id: int
    car_id: int
    created_by_user_id: int
    dealer_code: str
    last_modified_by_user: Optional[int] = None
    created_date:  Optional[datetime] = None
    created_by: Optional[str] = None
    soc_percentage: Optional[float] = None
    battery12v_Voltage : Optional[float] = None
    five_minutes_hybrid_check : Optional[bool] = None
    tire_pressure_dd: Optional[float] = None
    tire_pressure_de: Optional[float] = None
    tire_pressure_td: Optional[float] = None
    tire_pressure_te: Optional[float] = None
    user_comments: Optional[str] = None