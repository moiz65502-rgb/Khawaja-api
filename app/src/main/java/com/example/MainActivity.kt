package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import com.example.ui.LibasApp
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.LibasViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    
    val viewModel = ViewModelProvider(this)[LibasViewModel::class.java]
    
    setContent {
      val isDarkTheme by viewModel.isDarkTheme.collectAsState()
      
      MyApplicationTheme(darkTheme = isDarkTheme) {
        Surface {
          LibasApp(viewModel)
        }
      }
    }
  }
}
