from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from typing import List

from database.connection import get_db
from models.pdiModel import PDI
from models.carInfoModel import Cars
from schemas.pdiSchema import PDIBase, PDIResponse, PdiPending
from datetime import datetime
from sqlalchemy.sql import func


router = APIRouter(prefix="/pdi", tags=["PDI"])


@router.get("/", response_model=List[PDIBase])
def get_all_pdis(db: Session = Depends(get_db)):
    """Lista todos os registros de PDI"""
    return db.query(PDI).all()


@router.get("/{pdi_id}", response_model=PDIBase)
def get_pdi_by_pdi_id(pdi_id: str, db: Session = Depends(get_db)):
    """Lista todos os registros de PDI"""
    pdi = db.query(PDI).filter(PDI.pdi_id == pdi_id).first()
    if not pdi:
        raise HTTPException(status_code=404, detail="pdi not found")
    return pdi


# Latest pdis for a specific dealer
@router.get("/dealer/{dealer_code}", response_model=List[PDIBase])
def get_latest_pdi_for_dealer(dealer_code: str, db: Session = Depends(get_db)):
    """Get the most recent PDI for each car owned by a specific dealer"""

    # Criar um subquery que pega o PDI mais recente para cada carro do dealer
    latest_pdi_subquery = (
        db.query(PDI.car_id, func.max(PDI.created_date).label("max_created_date"))
        .join(Cars, Cars.car_id == PDI.car_id)
        .filter(Cars.dealer_code == dealer_code)
        .group_by(PDI.car_id)
        .subquery()
    )

    # Consulta principal para pegar os registros mais recentes
    latest_pdis = (
        db.query(PDI)
        .join(
            latest_pdi_subquery,
            (PDI.car_id == latest_pdi_subquery.c.car_id)
            & (PDI.created_date == latest_pdi_subquery.c.max_created_date),
        )
        .all()
    )

    if not latest_pdis:
        raise HTTPException(
            status_code=404, detail="No PDI records found for this dealer"
        )

    return latest_pdis


@router.get("/dealer/all/{dealer_code}", response_model=PDIBase)
def get_all_pdi_for_dealer(dealer_code: str, db: Session = Depends(get_db)):
    """Get all PDI records for a specific dealer"""
    return (
        db.query(PDI)
        .join(Cars, Cars.car_id == PDI.car_id)
        .filter(Cars.dealer_code == dealer_code)
        .all()
    )


@router.post("/", response_model=PDIBase)
def create_pdi(pdi: PDIResponse, db: Session = Depends(get_db)):
    """Create a new PDI record"""

    db_pdi = PDI(**pdi.model_dump())
    if db_pdi.soc_percentage is not None and db_pdi.soc_percentage <= 30:
        db_pdi.pending = True

    if db_pdi.pending == False:
        db_pdi.resolved_date = datetime.now()
    db.add(db_pdi)
    try:
        db.commit()
        db.refresh(db_pdi)
    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=401, detail=str(e))

    return db_pdi


@router.put("/{pdi_id}", response_model=PDIBase)
def update_pdi(pdi_id: int, pdi_update: PDIBase, db: Session = Depends(get_db)):
    """Update an existing PDI record"""
    db_pdi = db.query(PDI).filter(PDI.pdi_id == pdi_id).first()
    if not db_pdi:
        raise HTTPException(status_code=404, detail="PDI not found")

    # Update PDI fields
    for field, value in pdi_update.dict(exclude_unset=True).items():
        setattr(db_pdi, field, value)

    if db_pdi.soc_percentage <= 30:
        db_pdi.pending = True
        db_pdi.resolved_date = None
    else:
        db_pdi.pending = False
        if not db_pdi.resolved_date:
            db_pdi.resolved_date = db_pdi.created_date

    try:
        db.commit()
        db.refresh(db_pdi)
    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=400, detail=str(e))

    return db_pdi


# uptade pending
@router.put("/pending/{pdi_id}")
def update_pending_pdi(
    pdi_id: int, new_soc: PdiPending, db: Session = Depends(get_db)
):  # nenhum body deve ser passado
    db_pdi = db.query(PDI).filter(PDI.pdi_id == pdi_id).first()
    if not db_pdi:
        raise HTTPException(status_code=404, detail="PDI not found")
    db_pdi.soc_percentage = new_soc.new_soc

    db_pdi.pending = False
    db_pdi.resolved_date = datetime.now()

    try:
        db.commit()
        db.refresh(db_pdi)
    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=400, detail=str(e))

    return db_pdi
