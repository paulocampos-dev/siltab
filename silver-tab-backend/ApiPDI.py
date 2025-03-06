import os
import sys
from logs.loggerInit import logger
from logs.middleware import log_middleware
from fastapi import FastAPI, Request
from starlette.middleware.base import BaseHTTPMiddleware

if "--UAT" in sys.argv:
    os.environ["DATABASE_URL"] = (
        "oracle+cx_oracle://DEV_DMS:mcy216s-WK;l3W@10.42.253.92:1521/dms19g_pdb1"
        #"oracle://DEV_DMS:mcy216s-WK@10.42.253.92:1521/xe"
        #"oracle://DEV_DMS:mcy216s-WK@10.42.253.92:1521/dms19g_pdb1"
    )
elif "--local" in sys.argv:
    os.environ["DATABASE_URL"] = "oracle://c##silvertree:test123@localhost:1521/xe"
else:
    os.environ["DATABASE_URL"] = "oracle://c##silvertree:test123@localhost:1521/xe"

database_url = os.environ["DATABASE_URL"]
masked_db_url = database_url.replace(database_url.split('@')[0].split('//')[1], '***:***')
logger.info(f"Using DATABASE_URL: {masked_db_url}")


from fastapi import FastAPI
from routers.carInfoRouter import router as car_router
from routers.pdiRouter import router as pdi_router

import uvicorn

app = FastAPI(title="BYD PDI API")
logger.info("Starting API")
app.add_middleware(BaseHTTPMiddleware, dispatch = log_middleware)


app.include_router(car_router)
app.include_router(pdi_router)

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)