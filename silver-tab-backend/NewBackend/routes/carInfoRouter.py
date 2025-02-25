from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from typing import List

from database.connection import get_db
from models.carInfoModel import Cars
from schemas.carInfoSchema import CarsBase
from datetime import datetime






router = APIRouter(
    prefix = "/cars",
    tags = ["Cars"]
)



# get all cars
@router.get("/", response_model=List[CarsBase])
def get_all_cars(db: Session = Depends(get_db)):
    """List all cars"""
    return db.query(Cars).all()



#get car by chassi
@router.get("/{chassi_number}", response_model=CarsBase)
def get_car_by_chassi(chassi_number: str, db: Session = Depends(get_db)):
    """Get car by chassi number"""
    car = db.query(Cars).filter(Cars.chassi_number == chassi_number).first()
    if not car:
        raise HTTPException(status_code=404, detail="Car not found")
    return car




#get cars of the dealer 
#car/dealer/{dealer_code} ou car/{dealer_code}
@router.get("/dealer/{dealer_code}", response_model=List[CarsBase])
def get_cars_by_dealer(dealer_code: str, db: Session = Depends(get_db)):
    """Get all cars from a specific dealer"""
    dealer = db.query(Cars).filter(Cars.dealer_code == dealer_code).first()
    if not dealer:
        raise HTTPException(status_code=405, detail="dealer code not found")
    cars = db.query(Cars).filter(Cars.dealer_code == dealer_code).all()
    if not cars :
        raise HTTPException(status_code=405, detail="No car was found for this dealer")

    return cars
    


# create a new car
@router.post("/", response_model=CarsBase)
def create_car(car: CarsBase, db: Session = Depends(get_db)):
    """Cria um novo veículo"""
    new_car = Cars(**car.dict())
    db.add(new_car)
    db.commit()
    db.refresh(new_car)
    return new_car


@router.delete("/{chassi_number}", status_code=status.HTTP_204_NO_CONTENT)
def delete_car(chassi_number: int, db: Session = Depends(get_db)):
    """Delete a car by id"""
    car = db.query(Cars).filter(Cars.chassi_number == chassi_number).first()
    if not car:
        raise HTTPException(status_code=404, detail="Car not found")
    db.delete(car)
    db.commit()
    return {"detail": "Car deleted successfully"}


#update car to sold 
@router.put("/{chassi_number}", response_model=CarsBase)
def update_car_to_sold(chassi_number: int, date, db: Session = Depends(get_db)):
    """Update car to sold"""
    car = db.query(Cars).filter(Cars.chassi_number == chassi_number).first()
    if not car:
        raise HTTPException(status_code=404, detail="Car not found")
    car.is_sold = True
    car.sold_date = date # vai ser um parametro passado pelo app, ver como fazer
    db.commit()
    db.refresh(car)
    return car




