//Shopping manager iOS App
//Imports
import React, { Component } from 'react';
import { Alert, AppRegistry, Button, FlatList, Image, NavigatorIOS, PropTypes, StyleSheet, Text, TextInput, TouchableHighlight, View } from 'react-native';

export default class App extends Component 
{
  render() 
  {
    return (
      <NavigatorIOS
        initialRoute={{
          component: LoginPage,
          title: 'Budget Buddy',
        }}
        style={{flex: 1}}
      />
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
      <View style = {styles.container}>
        //Here he his 
        <Image 
          source = {require('./rl.jpg')}
          style = {styles.mainImg}
        />
        //Text boxes
        <BlankLine />
        <TextInput 
          style = {styles.textbox}
          value = {this.state.username}
          placeholder = "Username"
          onChangeText = {username => this.setState({username})}
          returnKeyType = "next"
          onSubmitEditing = { () => this.passwordInput.focus()}
        />
        <BlankLine />
        <TextInput 
          style = {styles.textbox}
          value = {this.state.password}
          placeholder = "Password"
          secureTextEntry = {true}
          onChangeText = {password => this.setState({password})}
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
    //Alert.alert("Login", user + " " + pass);
    this.props.navigator.push({
      title: 'Budget Mananger',
      component: MainPage,
      //leftButtonTitle: 'Back'
    });
  }
  
  handleSignUp = () => {
    const user = this.state.username;
    const pass = this.state.password;
    Alert.alert("Sign up", user + " " + pass);
  }
  
}

class MainPage extends Component {
  constructor(props){
    super(props);
    this.state = {
      txt: [],
      index: 1,
      name: '',
      qty: 0,
      price: 0,
      total: 0
    }
  }
  
  //Turn the user input into one string
  makeText(){
    let subtotal = this.state.qty * this.state.price;
    let newTotal = subtotal + this.state.total;
    let finalText = this.state.index + ") " + this.state.name + " $" + this.state.price + " x " + this.state.qty + " = $" + subtotal + "\n";
    
	this.state.txt.push(
      finalText
      )
      
    this.setState({
        index: this.state.index + 1,
        text: this.state.text,
        total: newTotal
      })
  }
  
  render(){
    return(
      <View style = {styles.container}>
        <Text>{"\n"}</Text>
       
        //Text boxes
        <TextInput 
          style = {styles.textbox}
          value = {this.state.name}
          placeholder = "Item Name"
          onChangeText = {name => this.setState({name})}
        />
        <TextInput 
          style = {styles.textbox}
          keyboardType = 'numeric'
          value = {this.state.qty}
          placeholder = "Quantity"
          onChangeText = {qty => this.setState({qty})}
        />
        <TextInput 
          style = {styles.textbox}
          keyboardType = 'numeric'
          value = {this.state.price}
          placeholder = "Price"
          onChangeText = {price => this.setState({price})}
        />
       
	      //Buttons
        <Button title = "Add" onPress = {() => this.makeText()} />
       
        <Text>{"Total: $" + this.state.total}</Text>

	      //Display as a list
        <FlatList
          data = {this.state.txt}
          extraData = {this.state.total}
          renderItem = {( {item, index} ) => <Text>{this.state.txt[index]}</Text>}
        />
      </View>
    )
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
    paddingTop: 50
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
    paddingLeft: 20,
    paddingRight: 20,
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
