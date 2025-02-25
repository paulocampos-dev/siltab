from fastapi import FastAPI, HTTPException, Depends, Response, status
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
from typing import List, Optional, Any
from pydantic import BaseModel
import uuid
from sqlalchemy.types import TypeDecorator, CLOB
from sqlalchemy.dialects.oracle import RAW
from sqlalchemy import Sequence
from sqlalchemy import Float
from routers.carInfoRouter import router as car_router
from routers.pdiRouter import router as pdi_router

# FastAPI app
app = FastAPI(title="BYD PDI API")

app.include_router(car_router)
app.include_router(pdi_router)


if __name__ == "__main__":
    import uvicorn

    uvicorn.run(app, host="0.0.0.0", port=8000)
