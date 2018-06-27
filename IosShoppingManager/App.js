//Shopping manager iOS App
//Imports
import React, { Component } from 'react';
import { Alert, AppRegistry, Button, Image, StyleSheet, Text, TextInput, TouchableHighlight, View } from 'react-native';

export default class App extends Component 
{
  render() 
  {
    return (
      <View style={styles.container}>
        <LoginPage />
      </View>
    );
  }
}

//Login/Sign up page
class LoginPage extends Component
{
  state = {
    username: '',
    password: ''
  }
  
  render()
  {
    return (
      <View>
        <Text style={styles.header}>Budget Manager</Text>
        //Here he his 
        <Image 
          source = {require('./rl.jpg')}
          style = {styles.mainImg}
        />
        //Text boxes
        <BlankLine />
        <TextInput 
          style={styles.textbox}
          value={this.state.username}
          placeholder = "Username"
          onChangeText={username => this.setState({username})}
          returnKeyType = "next"
          onSubmitEditing = { () => this.passwordInput.focus()}
        />
        <BlankLine />
        <TextInput 
          style={styles.textbox}
          value={this.state.password}
          placeholder = "Password"
          secureTextEntry = {true}
          onChangeText={password => this.setState({password})}
          ref = {(input) => this.passwordInput = input}
        />
        <BlankLine />
        
        //Login button
        <TouchableHighlight
           style = {styles.button}
           onPress = {this.handleLogin}
           underlayColor = '#fff'>
        <Text style = {styles.buttonText}>Login</Text>
        </TouchableHighlight>
        <BlankLine />
        
        //Sign Up button
         <TouchableHighlight
           style = {styles.button}
           onPress = {this.handleSignUp}
           underlayColor = '#fff'>
        <Text style = {styles.buttonText}>Sign up</Text>
        </TouchableHighlight>
      </View>
    )
  }
  
  handleLogin = () => {
    const user = this.state.username;
    const pass = this.state.password;
    Alert.alert("Login", user + " " + pass);
  }
  
  handleSignUp = () => {
    const user = this.state.username;
    const pass = this.state.password;
    Alert.alert("Sign up", user + " " + pass);
  }
  
}

//Easy way to create some spacing
function BlankLine(props)
{
  return  <Text>{"\n"}</Text>;
}

const styles = StyleSheet.create(
  {
  container: 
  {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
  header:
  {
    fontSize: 35
  },
  mainImg:
  {
    width: 200, 
    height: 200, 
    alignSelf: 'center'
  },
  textbox:
  {
    paddingLeft: 10,
    fontSize: 30,
    height: 50, 
    width: 250, 
    borderColor: 'gray',
    borderRadius: 7,
    borderWidth: 1
  },
  button:
  {
    marginLeft: 30,
    marginRight: 30,
    paddingTop: 10,
    paddingBottom: 10,
    backgroundColor:'black',
    borderRadius: 10,
    borderWidth: 1,
    borderColor: 'white'
  },
  buttonText:
  {
    color: 'white',
    fontSize: 30,
    textAlign: 'center'
  },
});
