from fastapi import FastAPI, HTTPException, Depends
from sqlalchemy import (
    create_engine,
    Column,
    Integer,
    String,
    SmallInteger,
    Boolean,
    ForeignKey,
    DateTime,
    Text,
)
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker, Session
from datetime import datetime
from typing import List, Optional
from pydantic import BaseModel
import uuid
from sqlalchemy.types import TypeDecorator
from sqlalchemy.dialects.oracle import RAW
from sqlalchemy import Sequence

# Database connection
SQLALCHEMY_DATABASE_URL = "oracle://c##silver_tab:silvertab@localhost:1521/xe"
engine = create_engine(SQLALCHEMY_DATABASE_URL)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()





# FastAPI app
app = FastAPI(title="BYD PDI API")

# Get all cars



if __name__ == "__main__":
    import uvicorn

    uvicorn.run(app, host="0.0.0.0", port=5000)
