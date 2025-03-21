import logging 
import sys


logger = logging.getLogger('apiPDI')

formatter = logging.Formatter(
    fmt= "%(asctime)s - %(levelname)s - %(message)s"
)


stream_handler = logging.StreamHandler(sys.stdout)
file_handler = logging.FileHandler("pdiAPI.log")

stream_handler.setFormatter(formatter)
file_handler.setFormatter(formatter)


if not logger.handlers:
    logger.addHandler(stream_handler)
    logger.addHandler(file_handler)



logger.setLevel(logging.INFO)