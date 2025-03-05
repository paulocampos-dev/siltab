import os
from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker

# Lê a variável de ambiente e usa ela como URL padrão;
# se não existir, cai no default do local mesmo.
SQLALCHEMY_DATABASE_URL = os.getenv(
    "DATABASE_URL", 
    "oracle://c##silvertree:test123@localhost:1521/xe"
)

engine = create_engine(SQLALCHEMY_DATABASE_URL)
print("[DEBUG] Usando URL:", SQLALCHEMY_DATABASE_URL)

SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()