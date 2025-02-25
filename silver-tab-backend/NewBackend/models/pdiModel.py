from sqlalchemy import (
    Column,
    Integer,
    String,    
    Float,
    Boolean,
    DateTime,
    Text,
)
from NewBackend.database.connection import Base



class PDI(Base):
    __tablename__ = "pdi_info"

    pdi_id = Column(Integer, primary_key=True, nullable=False)
    created_by_user_id = Column(Integer, nullable=False)
    dealer_code = Column(String, nullable=False)
    last_modified_by_user = Column(Integer)
    created_at = Column(DateTime, nullable=False)
    created_by = Column(String)
    soc_percentage = Column('SOC_PERCENTAGE', Float)
    battery12v_Voltage = Column('BATTERY12V_VOLTAGE', Integer)
    five_minutes_hybrid_check = Column('FIVE_MINUTES_HYBRID_check', Boolean)
    tire_pressure_dd = Column('TIRE_PRESSURE_DD', Float)
    tire_pressure_td = Column('TIRE_PRESSURE_TD', Float)
    tire_pressure_de = Column('TIRE_PRESSURE_DE', Float)
    tire_pressure_te = Column('TIRE_PRESSURE_TE', Float)
    user_comments = Column('USER_COMMENTS', Text)
