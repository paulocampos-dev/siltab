from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from typing import List

from database.connection import get_db
from models.carInfoModel import Cars
from models.carModelModel import CarModel
from models.pdiModel import PDI
from schemas.carInfoSchema import CarsBase, CarsUpload,  CarFullResponseForKotlin
from datetime import datetime






router = APIRouter(
    prefix = "/cars",
    tags = ["Cars"]
)



# get all cars
@router.get("/", response_model=List[CarFullResponseForKotlin])
def get_all_cars(db: Session = Depends(get_db)):
    """List all cars"""
    cars = (
        db.query(
            Cars.car_id,
            Cars.chassi_number,
            Cars.dealer_code,
            Cars.car_model_id,
            CarModel.car_model_name,
            Cars.is_sold,
            Cars.sold_date,
        )
        .join(CarModel, Cars.car_model_id == CarModel.car_model_id)
        .all()
    )

    result = []
    for car in cars:
        # Buscar os IDs dos PDIs relacionados ao carro
        pdis = db.query(PDI.pdi_id).filter(PDI.car_id == car.car_id).all()
        pdi_ids = [pdi.pdi_id for pdi in pdis]

        result.append(
            {
                "car_id": car.car_id,
                "chassi_number": car.chassi_number,
                "dealer_code": car.dealer_code,
                "car_model_id": car.car_model_id,
                "car_model_name": car.car_model_name,
                "is_sold": car.is_sold,
                "sold_date": car.sold_date,
                "Pdis": pdi_ids,  # Lista de IDs dos PDIs associados
            }
        )

    return result



#get car by chassi
@router.get("/{chassi_number}", response_model=CarFullResponseForKotlin)
def get_car_by_chassi(chassi_number: str, db: Session = Depends(get_db)):
    """Get car by chassi number"""
    car = (
        db.query(
            Cars.car_id,
            Cars.chassi_number,
            Cars.dealer_code,
            Cars.car_model_id,
            CarModel.car_model_name,
            Cars.is_sold,
            Cars.sold_date,
        )
        .join(CarModel, Cars.car_model_id == CarModel.car_model_id)
        .filter(Cars.chassi_number == chassi_number)
        .first()
    )
    if not car:
        raise HTTPException(status_code=404, detail="Car not found")

    # Buscar os IDs dos PDIs relacionados ao carro
    pdis = db.query(PDI.pdi_id).filter(PDI.car_id == car.car_id).all()
    pdi_ids = [pdi.pdi_id for pdi in pdis]

    return {
        "car_id": car.car_id,
        "chassi_number": car.chassi_number,
        "dealer_code": car.dealer_code,
        "car_model_id": car.car_model_id,
        "car_model_name": car.car_model_name,
        "is_sold": car.is_sold,
        "sold_date": car.sold_date,
        "Pdis": pdi_ids,  
    }
    




#get cars of the dealer 
@router.get("/dealer/{dealer_code}", response_model=List[CarFullResponseForKotlin])
def get_cars_by_dealer(dealer_code: str, db: Session = Depends(get_db)):
    """Get all cars from a specific dealer"""
    dealer = db.query(Cars).filter(Cars.dealer_code == dealer_code).first()
    if not dealer:
        raise HTTPException(status_code=405, detail="dealer code not found")
    cars = (
        db.query(
            Cars.car_id,
            Cars.chassi_number,
            Cars.dealer_code,
            Cars.car_model_id,
            CarModel.car_model_name,
            Cars.is_sold,
            Cars.sold_date,
        )
        .join(CarModel, Cars.car_model_id == CarModel.car_model_id)
        .filter(Cars.dealer_code == dealer_code)
        .all()
    )

    if not cars:
        raise HTTPException(status_code=404, detail="No cars found for this dealer")
    result = []
    for car in cars:
        # Buscar os IDs dos PDIs relacionados ao carro
        pdis = db.query(PDI.pdi_id).filter(PDI.car_id == car.car_id).all()
        pdi_ids = [pdi.pdi_id for pdi in pdis]

        result.append(
            {
                "car_id": car.car_id,
                "chassi_number": car.chassi_number,
                "dealer_code": car.dealer_code,
                "car_model_id": car.car_model_id,
                "car_model_name": car.car_model_name,
                "is_sold": car.is_sold,
                "sold_date": car.sold_date,
                "Pdis": pdi_ids,  # Lista de IDs dos PDIs associados
            }
        )

    return result
    


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
def delete_car(chassi_number: str, db: Session = Depends(get_db)):
    """Delete a car by id"""
    car = db.query(Cars).filter(Cars.chassi_number == chassi_number).first()
    if not car:
        raise HTTPException(status_code=404, detail="Car not found")
    db.delete(car)
    db.commit()
    return {"detail": "Car deleted successfully"}


#update car to sold 


@router.put("/{chassi_number}", response_model=CarsBase)
def update_car_to_sold(chassi_number: str, date: CarsUpload, db: Session = Depends(get_db)):
    """Update car to sold"""
    car = db.query(Cars).filter(Cars.chassi_number == chassi_number).first()
    if not car:
        raise HTTPException(status_code=404, detail="Car not found")
    car.is_sold = True
    car.sold_date = date.sold_date # vai ser um parametro passado pelo app, ver como fazer
    db.commit()
    db.refresh(car)
    return car




