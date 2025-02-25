from sqlalchemy import (
    Column,
    Integer,
    String,    
    Float,
    Boolean,
    DateTime,
    Text,
    ForeignKey
)
from database.connection import Base



class PDI(Base):
    __tablename__ = "pdi_info"

    pdi_id = Column(Integer, primary_key=True, nullable=False, autoincrement=True)
    car_id = Column(Integer, ForeignKey("car_info.car_id"), nullable=False)  
    created_by_user_id = Column('CREATE_BY_USER_ID', Integer, nullable=False)
    dealer_code = Column(String, nullable=False)
    last_modified_by_user = Column('LAST_MODIFIED_BY_USER_ID', Integer)
    created_date = Column(DateTime, nullable=False)
    create_by = Column(String)
    soc_percentage = Column('SOC_PERCENTAGE', Float)
    battery12v_Voltage = Column('BATTERY12V_VOLTAGE', Float)
    five_minutes_hybrid_check = Column('FIVE_MINUTES_HYBRID_CHECK', Boolean)
    tire_pressure_dd = Column('TIRE_PRESSURE_DD', Float)
    tire_pressure_td = Column('TIRE_PRESSURE_TD', Float)
    tire_pressure_de = Column('TIRE_PRESSURE_DE', Float)
    tire_pressure_te = Column('TIRE_PRESSURE_TE', Float)
    user_comments = Column('USER_COMMENTS', Text)
