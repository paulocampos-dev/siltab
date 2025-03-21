from fastapi import Request
from logs.loggerInit import logger
import time




async def log_middleware(request: Request, call_next):
    start = time.time()
    response = await call_next(request)

    request_time = time.time() - start
    log_dict ={
        'url': request.url.path,
        "method": request.method,
        "status_code": response.status_code,
        "client_ip": request.client.host,
        'request_time': request_time,
    }
    
    logger.info("Request processed", extra = log_dict)

    
    return response
