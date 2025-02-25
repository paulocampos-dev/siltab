from typing import List, Optional, Any
from pydantic import BaseModel
from datetime import datetime


class PDIBase(BaseModel):
    #pdi_id: int
    created_by_user_id: int
    dealer_code: str
    last_modified_by_user: Optional[int] = None
    created_at: datetime
    created_by: Optional[str] = None
    soc_percentage: Optional[float] = None
    battery12v: Optional[int] = None
    five_minutes_hybrid: Optional[bool] = None
    tire_pressure_dd: Optional[float] = None
    tire_pressure_de: Optional[float] = None
    tire_pressure_td: Optional[float] = None
    tire_pressure_te: Optional[float] = None
    user_comments: Optional[str] = None