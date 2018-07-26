# Shopping-Manager
Credit: Harrison Black, Jacob Chaulk, Thomas Serrano, Sixiong Yang, Jesus Duran, Terrell Richardson, Tuan Pham

## Wep App Installation
1. Clone project from github (git clone)
2. Install pip (pip: a package manager)

    ```curl https://bootstrap.pypa.io/get-pip.py -o get-pip.py```

	```sudo python get-pip.py```
3. Install virtualenv (development environment)

    ```pip install pipenv```
4. ```pipenv install```
5. ```pipenv shell```
6. ```python manage.py migrate```
7. To run the app: ```python manage.py runserver```
8. You will see a portal to the project on terminal (something like ```http://127.0.0.1:8000/```). 

## Heroku Deploy
1. ```git add.```
2. ```git commit -m <message>```
3. ```git push heroku master && heroku run python manage.py migrate```