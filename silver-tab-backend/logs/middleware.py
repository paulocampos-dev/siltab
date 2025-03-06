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
        'request_time': request_time
    }
    
    logger.info(log_dict, extra = log_dict)

    
    return response
