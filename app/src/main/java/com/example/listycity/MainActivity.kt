package com.example.listycity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.example.listycity.ui.theme.ListyCityTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.Arrangement

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val cityRepository = CityRepository()

        setContent {
            ListyCityTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CityListScreen(
                        cities = cityRepository.cities,
                        onAddCity = { cityRepository.addCity(it) },
                        onDeleteCity = { cityRepository.deleteCity(it) },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ListyCityTheme {
        Greeting("Android")
    }
}

class CityRepository {
    private val _cities = mutableStateListOf(
        "Edmonton", "Vancouver", "Moscow",
        "Sydney", "Berlin", "Vienna",
        "Tokyo", "Beijing", "Osaka",
        "New Delhi"
    )

    val cities: List<String>
        get() = _cities

    fun addCity(city: String) {
        _cities.add(city)
    }

    fun deleteCity(city: String) {
        _cities.remove(city)
    }
}

@Composable
fun CityListScreen(
    cities: List<String>,
    onAddCity: (String) -> Unit,
    onDeleteCity: (String) -> Unit,
    modifier: Modifier = Modifier,
    statusViewModel: StatusViewModel = viewModel()
) {
    var newCityName by remember {mutableStateOf(value = "") }
    var addCityClick by remember {mutableStateOf<String?>(value=null) } // don't need view model because
                                                                        // this is all in one composable

    Column(modifier = modifier.fillMaxSize()) {
        Row(modifier = Modifier.padding(horizontal = 16.dp)) {
            if (addCityClick != null) {   // only show text bar w/ confirm if user chooses to add city
                OutlinedTextField(
                    value = newCityName,
                    onValueChange = { newCityName = it },
                    label = { Text("City name") },
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        if (newCityName.isNotBlank()) {
                            onAddCity(newCityName)
                            newCityName = ""
                        }
                    }
                ) {
                    Text("Confirm")
                }

                Spacer(modifier = Modifier.width(2.dp))

                Button (   // done button that lets users stop adding cities and remove text bar
                    onClick = {
                        addCityClick = null
                    }
                ) {
                    Text("Done")
                }
            }
        }
        Spacer(modifier = Modifier.width(15.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    addCityClick = ""
                }
            ) {
                Text("Add City")
            }

            Spacer(modifier = Modifier.width(20.dp))

            Button(
                onClick = {
                    if (statusViewModel.deleteCity.isNotBlank()) {
                        onDeleteCity(statusViewModel.deleteCity)
                        statusViewModel.deleteCity = ""  // make the string blank to reuse once city has been deleted
                    }
                }
            ) {
                Text("Delete City")
            }
        }
        LazyColumn(modifier = modifier.fillMaxSize()) {
            items(cities) {city ->
                CityRow(city = city,
                    statusViewModel = statusViewModel
                )
            }
        }
    }
}

@Composable
fun CityRow(
    city: String,
    statusViewModel: StatusViewModel
) {
    Button(
        onClick = {
            statusViewModel.deleteCity = city // pass a string so that variable is not empty and delete button can see that city has been selected
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 18.dp,
                vertical = 14.dp
            )
    ) {
        Text(
            text = city,
            fontSize = 28.sp
        )
    }
}

class StatusViewModel: ViewModel(){ // view model allows data to move between composable so that city button can communicate to the delete button
    var deleteCity by mutableStateOf("")
}
