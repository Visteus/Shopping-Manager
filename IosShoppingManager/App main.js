import React, { Component } from 'react';
import { AppRegistry, View, Text, Button, TextInput, StyleSheet} from 'react-native';

export default class App extends Component {
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
    
    this.state.txt.push(
      <Text>{this.state.index + ") " + this.state.name + " $" + this.state.price + " x " + this.state.qty + " = $" + subtotal + "\n"}</Text>
      )
      
    this.setState({
        index: this.state.index + 1,
        text: this.state.text,
        total: newTotal
      })
  }
  
  render(){
    return(
      <View>
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
       
       <Button title = "Add" onPress = {() => this.makeText()} />
       
	   //Display total and list
       <Text>{"Total: $" + this.state.total}</Text>
       <Text>{this.state.txt}</Text>
       
      </View>
    )
  }
}

const styles = StyleSheet.create(
  {
  textbox:
  {
    paddingLeft: 10,
    fontSize: 30,
    height: 50, 
    width: 250, 
    borderColor: 'gray',
    borderRadius: 7,
    borderWidth: 1
  }
});
