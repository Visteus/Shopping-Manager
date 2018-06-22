# Shopping-Manager
Credit: Harrison Black, Jacob Chaulk, Thomas Serrano, Sixiong Yang, Jesus Duran, Terrell Richardson, Tuan Pham

## Mobile App Installation
## Wep App Installation
1. Clone project from github (git clone)
2. Install pip (pip: a package manager)

    ```curl https://bootstrap.pypa.io/get-pip.py -o get-pip.py```

	```sudo python get-pip.py```
3. Install virtualenv (development environment)

    ```pip install virtualenv```
4. ```cd web-app```
5. ```virtualenv env -p python3```
6. ```source env/bin/activate```
7. ```pip install -r requirements.txt```
8. ```cd shoppingmanager```
9. ```python manage.py migrate```
10. To run the app: ```python manage.py runserver```
11. You will see a portal to the project on terminal (something like ```http://127.0.0.1:8000/```). 