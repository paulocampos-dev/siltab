# Silver Tab

Here is the code for our app in Kotlin.

This is a Kotlin APP made to capture PDI (Pre-Delivery Inspection) information from dealers.

This project consists in a Kotlin APP and in a Python API to manage the PDI information.

## How to run the app?
If you'd like to run the app on an emulator, you just have to git clone this repo and open it on Android Studio them click 'run app' icon on the top bar (also Shift+F10).

## How to run the API?
In the 'silver-tab-backend' directory is our Python API that manages PDI.

We are using the database addresses that were defined on the JWT service. The addresses are on the file ApiPDI.py

To install the Python libraries you can use

```shell
pip install -r requirements
```

### Running on locally
```shell
python3 ./ApiPDI.py
```

### Running on UAT
```shell
python3 ./ApiPDI.py --UAT
```

### Running on PROD
```shell
python3 ./ApiPDI.py --PROD
```