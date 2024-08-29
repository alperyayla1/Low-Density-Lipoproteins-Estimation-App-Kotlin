package com.example.spinner
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import kotlin.math.*
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.ui.draw.clip

import androidx.compose.ui.text.font.FontWeight
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LDLCalculatorApp()
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LDLCalculatorApp() {
    var totalCholesterol by remember { mutableStateOf("") }
    var hdlCholesterol by remember { mutableStateOf("") }
    var triglycerides by remember { mutableStateOf("") }
    var ldlResults by remember { mutableStateOf(mapOf<String, String>()) }
    var nonHDLValue by remember { mutableStateOf("") }
    var nonHDLUnit by remember { mutableStateOf("") }
    var totalCholesterolUnit by remember { mutableStateOf("mg/dL") }
    var hdlCholesterolUnit by remember { mutableStateOf("mg/dL") }
    var triglyceridesUnit by remember { mutableStateOf("mg/dL") }
    var resultUnit by remember { mutableStateOf("mg/dL") }
    var message by remember { mutableStateOf<Message>(Message.Welcome) }
    var hasCalculated by remember { mutableStateOf(false) }
    //var errorMessage by remember { mutableStateOf("") }
    fun calculateAndUpdateLDL() {
        //errorMessage = ""

        val tcValue = totalCholesterol.toDoubleOrNull()
        val hdlValue = hdlCholesterol.toDoubleOrNull()
        val tgValue = triglycerides.toDoubleOrNull()

        if (tcValue == null || hdlValue == null || tgValue == null) {
            message = Message.Error("Please enter valid numeric values for all fields.")
            return
        }

        if (tcValue <= 0 || hdlValue <= 0 || tgValue <= 0) {
            //message = Message.Error("All values must be greater than 0.")
            return
        }

        if (hdlValue >= tcValue) {
            //message = Message.Error("HDL Cholesterol cannot be greater than or equal to Total Cholesterol.")
            return
        }

        val tcMgDL = tcValue
        val hdlMgDL = hdlValue
        val tgMgDL = tgValue

        val methods = listOf("Friedewald Formula", "Sampson-NIH Formula", "Yayla-TR Formula", "Extended Martin Formula")
        ldlResults = methods.associateWith { method ->
            val ldlInMgDL = calculateLDL(tcMgDL, hdlMgDL, tgMgDL, method)
            if (ldlInMgDL < 0) {
                "N/A"
            } else {
                if (resultUnit == "mmol/L") {
                    String.format("%.2f", convertMgDLToMmolL(ldlInMgDL))
                } else {
                    String.format("%.2f", ldlInMgDL)
                }
            }
        }

        val nonHDLInMgDL = calculateNonHDL(tcMgDL, hdlMgDL)
        if (resultUnit == "mmol/L") {
            nonHDLValue = String.format("%.2f", convertMgDLToMmolL(nonHDLInMgDL))
            nonHDLUnit = "mmol/L"
        } else {
            nonHDLValue = String.format("%.2f", nonHDLInMgDL)
            nonHDLUnit = "mg/dL"
        }

        /* Add warnings based on LDL levels (using Friedewald method for simplicity)
        val ldlInMgDL = calculateLDL(tcMgDL, hdlMgDL, tgMgDL, "Friedewald")
        val ldlWarning = when {
            ldlInMgDL < 70 -> Message.Success("Your LDL level is optimal.")
            ldlInMgDL < 100 -> Message.Success("Your LDL level is near optimal.")
            ldlInMgDL < 130 -> Message.Warning("Your LDL level is borderline high.")
            ldlInMgDL < 160 -> Message.Warning("Your LDL level is high.")
            else -> Message.Warning("Your LDL level is very high.")
        }
        message = ldlWarning
        */
        hasCalculated = true
    }

    fun resetFields() {
        totalCholesterol = ""
        hdlCholesterol = ""
        triglycerides = ""
        ldlResults = mapOf()
        nonHDLValue = ""
        totalCholesterolUnit = "mg/dL"
        hdlCholesterolUnit = "mg/dL"
        triglyceridesUnit = "mg/dL"
        resultUnit = "mg/dL"
        //message = Message.Welcome
        hasCalculated = false
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "LDL Calculator",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = { resetFields() }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            //MessageDisplay(message)
            InputFieldWithUnitSpinner(
                value = totalCholesterol,
                onValueChange = {
                    totalCholesterol = it
                    calculateAndUpdateLDL()
                },
                label = "Total Cholesterol",
                unit = totalCholesterolUnit,
                onUnitChange = {
                    totalCholesterolUnit = it
                    calculateAndUpdateLDL()
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            InputFieldWithUnitSpinner(
                value = hdlCholesterol,
                onValueChange = {
                    hdlCholesterol = it
                    calculateAndUpdateLDL()
                },
                label = "HDL Cholesterol",
                unit = hdlCholesterolUnit,
                onUnitChange = {
                    hdlCholesterolUnit = it
                    calculateAndUpdateLDL()
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            InputFieldWithUnitSpinner(
                value = triglycerides,
                onValueChange = {
                    triglycerides = it
                    calculateAndUpdateLDL()
                },
                label = "Triglycerides",
                unit = triglyceridesUnit,
                onUnitChange = {
                    triglyceridesUnit = it
                    calculateAndUpdateLDL()
                },
                isTriglycerides = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Non-HDL Cholesterol:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = nonHDLValue,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = nonHDLUnit,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { calculateAndUpdateLDL() },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Calculate LDL", style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(modifier = Modifier.height(32.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "LDL Cholesterol Results:",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Result Unit:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        UnitSpinner(
                            currentUnit = resultUnit,
                            onUnitSelected = {
                                resultUnit = it
                                calculateAndUpdateLDL()
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    ldlResults.forEach { (method, result) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "$method:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "$result $resultUnit",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
    }
}

sealed class Message {
    object Welcome : Message()
    data class Error(val message: String) : Message()
    data class Success(val message: String) : Message()
    data class Warning(val message: String) : Message()
}

@Composable
fun MessageDisplay(message: Message) {
    val (backgroundColor, contentColor, icon) = when (message) {
        is Message.Welcome -> Triple(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer, Icons.Default.Info)
        is Message.Error -> Triple(MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer, Icons.Default.Warning)
        is Message.Success -> Triple(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer, Icons.Default.CheckCircle)
        is Message.Warning -> Triple(MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer, Icons.Default.Warning)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Message Icon",
                tint = contentColor
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = when (message) {
                    is Message.Welcome -> "Welcome! Enter your cholesterol values and click 'Calculate LDL' to see the results."
                    is Message.Error -> message.message
                    is Message.Success -> message.message
                    is Message.Warning -> message.message
                },
                color = contentColor,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// ... (keep other functions like MethodSpinner, calculateLDL, InputFieldWithUnitSpinner, and UnitSpinner as they were)

fun convertMgDLToMmolL(mgDL: Double): Double {
    return (mgDL / 38.67).roundToDecimalPlaces(2)
}

fun convertMmolLToMgDL(mmolL: Double): Double {
    return (mmolL * 38.67).roundToDecimalPlaces(2)
}

fun convertMgDLToMmolLTriglycerides(mgDL: Double): Double {
    return (mgDL / 88.57).roundToDecimalPlaces(2)
}

fun convertMmolLToMgDLTriglycerides(mmolL: Double): Double {
    return (mmolL * 88.57).roundToDecimalPlaces(2)
}

fun Double.roundToDecimalPlaces(decimalPlaces: Int): Double {
    val factor = 10.0.pow(decimalPlaces.toDouble())
    return (this * factor).roundToInt() / factor
}
fun calculateNonHDL(totalCholesterol: Double, hdlCholesterol: Double): Double {
    return totalCholesterol - hdlCholesterol
}

@Composable

fun MethodSpinner(
    currentMethod: String,
    onMethodSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val methods = listOf("Friedewald", "Sampson-NIH", "Yayla TR", "Martin Extended")

    Box {
        Button(
            onClick = { expanded = true },
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Text(currentMethod, color = MaterialTheme.colorScheme.onSecondaryContainer)
            Icon(
                Icons.Filled.ArrowDropDown,
                contentDescription = "Choose method",
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            methods.forEach { method ->
                DropdownMenuItem(
                    text = { Text(method) },
                    onClick = {
                        onMethodSelected(method)
                        expanded = false
                    }
                )
            }
        }
    }
}
val martinTable = mapOf(
    7 to mapOf(100 to 3.5, 129 to 3.4, 159 to 3.3, 189 to 3.3, 219 to 3.2, 220 to 3.1),
    50 to mapOf(100 to 4.0, 129 to 3.9, 159 to 3.7, 189 to 3.6, 219 to 3.6, 220 to 3.4),
    57 to mapOf(100 to 4.3, 129 to 4.1, 159 to 4.0, 189 to 3.9, 219 to 3.8, 220 to 3.6),
    62 to mapOf(100 to 4.5, 129 to 4.3, 159 to 4.1, 189 to 4.0, 219 to 3.9, 220 to 3.9),
    67 to mapOf(100 to 4.7, 129 to 4.4, 159 to 4.3, 189 to 4.2, 219 to 4.1, 220 to 3.9),
    72 to mapOf(100 to 4.8, 129 to 4.6, 159 to 4.4, 189 to 4.2, 219 to 4.2, 220 to 4.1),
    76 to mapOf(100 to 4.9, 129 to 4.6, 159 to 4.5, 189 to 4.3, 219 to 4.3, 220 to 4.2),
    80 to mapOf(100 to 5.0, 129 to 4.8, 159 to 4.6, 189 to 4.4, 219 to 4.3, 220 to 4.2),
    84 to mapOf(100 to 5.1, 129 to 4.8, 159 to 4.6, 189 to 4.5, 219 to 4.4, 220 to 4.3),
    88 to mapOf(100 to 5.2, 129 to 4.9, 159 to 4.7, 189 to 4.6, 219 to 4.4, 220 to 4.3),
    93 to mapOf(100 to 5.3, 129 to 5.0, 159 to 4.8, 189 to 4.7, 219 to 4.5, 220 to 4.4),
    97 to mapOf(100 to 5.4, 129 to 5.1, 159 to 4.8, 189 to 4.7, 219 to 4.5, 220 to 4.3),
    101 to mapOf(100 to 5.5, 129 to 5.2, 159 to 5.0, 189 to 4.7, 219 to 4.6, 220 to 4.5),
    106 to mapOf(100 to 5.6, 129 to 5.3, 159 to 5.0, 189 to 4.8, 219 to 4.6, 220 to 4.5),
    111 to mapOf(100 to 5.7, 129 to 5.4, 159 to 5.1, 189 to 4.9, 219 to 4.7, 220 to 4.5),
    116 to mapOf(100 to 5.8, 129 to 5.5, 159 to 5.2, 189 to 5.0, 219 to 4.8, 220 to 4.6),
    121 to mapOf(100 to 6.0, 129 to 5.5, 159 to 5.3, 189 to 5.0, 219 to 4.8, 220 to 4.6),
    127 to mapOf(100 to 6.1, 129 to 5.7, 159 to 5.3, 189 to 5.1, 219 to 4.9, 220 to 4.7),
    133 to mapOf(100 to 6.2, 129 to 5.8, 159 to 5.4, 189 to 5.2, 219 to 5.0, 220 to 4.7),
    139 to mapOf(100 to 6.3, 129 to 5.9, 159 to 5.6, 189 to 5.3, 219 to 5.0, 220 to 4.8),
    147 to mapOf(100 to 6.5, 129 to 6.0, 159 to 5.7, 189 to 5.4, 219 to 5.1, 220 to 4.8),
    155 to mapOf(100 to 6.7, 129 to 6.2, 159 to 5.8, 189 to 5.4, 219 to 5.2, 220 to 4.9),
    164 to mapOf(100 to 6.8, 129 to 6.3, 159 to 5.9, 189 to 5.5, 219 to 5.3, 220 to 5.0),
    174 to mapOf(100 to 7.0, 129 to 6.5, 159 to 6.0, 189 to 5.7, 219 to 5.4, 220 to 5.1),
    186 to mapOf(100 to 7.3, 129 to 6.7, 159 to 6.2, 189 to 5.8, 219 to 5.5, 220 to 5.2),
    202 to mapOf(100 to 7.6, 129 to 6.9, 159 to 6.4, 189 to 6.0, 219 to 5.6, 220 to 5.3),
    221 to mapOf(100 to 8.0, 129 to 7.2, 159 to 6.6, 189 to 6.2, 219 to 5.9, 220 to 5.4),
    248 to mapOf(100 to 8.5, 129 to 7.6, 159 to 7.0, 189 to 6.5, 219 to 6.1, 220 to 5.6),
    293 to mapOf(100 to 9.5, 129 to 8.3, 159 to 7.5, 189 to 7.0, 219 to 6.5, 220 to 5.9),
    400 to mapOf(100 to 10.4, 129 to 8.7, 159 to 7.9, 189 to 7.3, 219 to 6.7, 220 to 6.1),
    410 to mapOf(100 to 10.7, 129 to 8.9, 159 to 7.9, 189 to 7.3, 219 to 6.7, 220 to 6.0),
    420 to mapOf(100 to 10.3, 129 to 8.9, 159 to 7.9, 189 to 7.4, 219 to 6.8, 220 to 6.0),
    430 to mapOf(100 to 11.2, 129 to 8.9, 159 to 8.0, 189 to 7.3, 219 to 6.8, 220 to 6.0),
    440 to mapOf(100 to 12.0, 129 to 9.0, 159 to 8.0, 189 to 7.5, 219 to 6.9, 220 to 6.0),
    450 to mapOf(100 to 11.3, 129 to 9.3, 159 to 8.2, 189 to 7.4, 219 to 7.0, 220 to 6.0),
    460 to mapOf(100 to 12.3, 129 to 9.2, 159 to 8.3, 189 to 7.7, 219 to 6.9, 220 to 6.1),
    470 to mapOf(100 to 10.6, 129 to 9.3, 159 to 8.3, 189 to 7.6, 219 to 7.0, 220 to 6.0),
    480 to mapOf(100 to 11.7, 129 to 9.3, 159 to 8.4, 189 to 7.6, 219 to 7.1, 220 to 6.1),
    490 to mapOf(100 to 11.6, 129 to 9.6, 159 to 8.4, 189 to 7.6, 219 to 7.2, 220 to 6.2),
    500 to mapOf(100 to 12.1, 129 to 9.2, 159 to 8.4, 189 to 7.5, 219 to 7.1, 220 to 6.2),
    510 to mapOf(100 to 12.3, 129 to 9.9, 159 to 8.5, 189 to 7.9, 219 to 7.1, 220 to 6.3),
    520 to mapOf(100 to 12.0, 129 to 9.8, 159 to 8.7, 189 to 7.7, 219 to 7.1, 220 to 6.3),
    530 to mapOf(100 to 12.0, 129 to 9.8, 159 to 8.7, 189 to 7.8, 219 to 7.2, 220 to 6.3),
    540 to mapOf(100 to 11.3, 129 to 10.0, 159 to 8.8, 189 to 7.8, 219 to 7.4, 220 to 6.3),
    550 to mapOf(100 to 12.2, 129 to 10.2, 159 to 8.8, 189 to 8.0, 219 to 7.4, 220 to 6.2),
    560 to mapOf(100 to 13.8, 129 to 10.2, 159 to 8.7, 189 to 8.1, 219 to 7.2, 220 to 6.2),
    570 to mapOf(100 to 15.4, 129 to 10.4, 159 to 8.9, 189 to 8.0, 219 to 7.3, 220 to 6.2),
    580 to mapOf(100 to 12.7, 129 to 10.5, 159 to 9.1, 189 to 8.3, 219 to 7.3, 220 to 6.4),
    590 to mapOf(100 to 12.5, 129 to 10.5, 159 to 9.2, 189 to 8.3, 219 to 7.2, 220 to 5.9),
    600 to mapOf(100 to 13.7, 129 to 10.5, 159 to 8.9, 189 to 8.2, 219 to 7.6, 220 to 6.3),
    610 to mapOf(100 to 15.4, 129 to 10.5, 159 to 9.1, 189 to 8.4, 219 to 7.5, 220 to 6.4),
    620 to mapOf(100 to 16.4, 129 to 11.3, 159 to 9.2, 189 to 8.5, 219 to 7.5, 220 to 6.4),
    630 to mapOf(100 to 14.1, 129 to 11.6, 159 to 9.4, 189 to 8.2, 219 to 7.3, 220 to 6.2),
    640 to mapOf(100 to 14.8, 129 to 11.0, 159 to 9.1, 189 to 8.1, 219 to 7.5, 220 to 6.6),
    650 to mapOf(100 to 14.2, 129 to 11.0, 159 to 9.2, 189 to 8.3, 219 to 7.5, 220 to 6.4),
    660 to mapOf(100 to 15.0, 129 to 10.9, 159 to 9.2, 189 to 8.3, 219 to 7.5, 220 to 6.5),
    670 to mapOf(100 to 14.2, 129 to 11.0, 159 to 9.3, 189 to 8.6, 219 to 7.6, 220 to 6.7),
    680 to mapOf(100 to 16.7, 129 to 11.5, 159 to 9.8, 189 to 8.3, 219 to 7.4, 220 to 6.7),
    690 to mapOf(100 to 15.0, 129 to 11.6, 159 to 9.8, 189 to 8.4, 219 to 7.8, 220 to 6.5),
    700 to mapOf(100 to 16.6, 129 to 11.5, 159 to 9.5, 189 to 8.5, 219 to 7.8, 220 to 6.9),
    710 to mapOf(100 to 14.5, 129 to 10.9, 159 to 9.7, 189 to 8.5, 219 to 7.8, 220 to 6.4),
    720 to mapOf(100 to 16.5, 129 to 11.7, 159 to 9.5, 189 to 8.5, 219 to 7.6, 220 to 6.6),
    730 to mapOf(100 to 18.2, 129 to 12.2, 159 to 9.9, 189 to 8.9, 219 to 8.2, 220 to 6.6),
    740 to mapOf(100 to 17.5, 129 to 11.7, 159 to 9.9, 189 to 8.5, 219 to 7.9, 220 to 6.6),
    750 to mapOf(100 to 17.5, 129 to 12.9, 159 to 10.2, 189 to 8.8, 219 to 8.1, 220 to 6.4),
    760 to mapOf(100 to 19.2, 129 to 11.4, 159 to 9.9, 189 to 8.7, 219 to 8.3, 220 to 6.5),
    770 to mapOf(100 to 17.3, 129 to 13.4, 159 to 10.4, 189 to 8.6, 219 to 8.2, 220 to 6.7),
    780 to mapOf(100 to 23.9, 129 to 12.3, 159 to 10.4, 189 to 9.1, 219 to 7.9, 220 to 6.7),
    790 to mapOf(100 to 15.6, 129 to 13.0, 159 to 10.7, 189 to 8.7, 219 to 8.0, 220 to 6.7),
    13975 to mapOf(100 to 11.9, 129 to 10.0, 159 to 8.8, 189 to 8.1, 219 to 7.5, 220 to 6.7)
)
fun getTriglycerideBracket(triglycerides: Int): Int {
    return when (triglycerides) {
        in 7..49 -> 7
        in 50..56 -> 50
        in 57..61 -> 57
        in 62..66 -> 62
        in 67..71 -> 67
        in 72..75 -> 72
        in 76..79 -> 76
        in 80..83 -> 80
        in 84..87 -> 84
        in 88..92 -> 88
        in 93..96 -> 93
        in 97..100 -> 97
        in 101..105 -> 101
        in 106..110 -> 106
        in 111..115 -> 111
        in 116..120 -> 116
        in 121..126 -> 121
        in 127..132 -> 127
        in 133..138 -> 133
        in 139..146 -> 139
        in 147..154 -> 147
        in 155..163 -> 155
        in 164..173 -> 164
        in 174..185 -> 174
        in 186..201 -> 186
        in 202..220 -> 202
        in 221..247 -> 221
        in 248..292 -> 248
        in 293..399 -> 293
        in 400..409 -> 400
        in 410..419 -> 410
        in 420..429 -> 420
        in 430..439 -> 430
        in 440..449 -> 440
        in 450..459 -> 450
        in 460..469 -> 460
        in 470..479 -> 470
        in 480..489 -> 480
        in 490..499 -> 490
        in 500..509 -> 500
        in 510..519 -> 510
        in 520..529 -> 520
        in 530..539 -> 530
        in 540..549 -> 540
        in 550..559 -> 550
        in 560..569 -> 560
        in 570..579 -> 570
        in 580..589 -> 580
        in 590..599 -> 590
        in 600..609 -> 600
        in 610..619 -> 610
        in 620..629 -> 620
        in 630..639 -> 630
        in 640..649 -> 640
        in 650..659 -> 650
        in 660..669 -> 660
        in 670..679 -> 670
        in 680..689 -> 680
        in 690..699 -> 690
        in 700..709 -> 700
        in 710..719 -> 710
        in 720..729 -> 720
        in 730..739 -> 730
        in 740..749 -> 740
        in 750..759 -> 750
        in 760..769 -> 760
        in 770..779 -> 770
        in 780..789 -> 780
        in 790..799 -> 790
        in 790..13975 ->13975
        else -> martinTable.keys.sortedDescending().find { it <= triglycerides } ?: martinTable.keys.first()
    }
}

fun getMartinDivisor(triglycerides: Int, nonHDLC: Int): Double {
    val tgKey = getTriglycerideBracket(triglycerides)
    val row = martinTable[tgKey]!!

    return when {
        nonHDLC < 100 -> row[100]!!
        nonHDLC < 130 -> row[129]!!
        nonHDLC < 160 -> row[159]!!
        nonHDLC < 190 -> row[189]!!
        nonHDLC < 220 -> row[219]!!
        else -> row[220]!!
    }
}
fun calculateLDL(totalCholesterol: Double, hdlCholesterol: Double, triglycerides: Double, method: String): Double {
    return when (method) {
        "Friedewald Formula" -> totalCholesterol - hdlCholesterol - (triglycerides / 5)
        "Sampson-NIH Formula" -> (totalCholesterol / 0.948) - (hdlCholesterol / 0.971) - ((triglycerides / 8.56) + (triglycerides * ((totalCholesterol - hdlCholesterol)/2140)) - (triglycerides * triglycerides/16100)) - 9.44
        "Yayla-TR Formula" -> totalCholesterol - hdlCholesterol - (((sqrt(triglycerides)*totalCholesterol) / 100))
        "Extended Martin Formula" -> {
            val nonHDLC = totalCholesterol - hdlCholesterol
            val divisor = getMartinDivisor(triglycerides.toInt(), nonHDLC.toInt())
            totalCholesterol - hdlCholesterol - (triglycerides / divisor)
        }
        else -> 0.0 // Default to 0 if an unknown method is selected
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputFieldWithUnitSpinner(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    unit: String,
    onUnitChange: (String) -> Unit,
    isTriglycerides: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = if (unit == "mmol/L") {
                    val mgdlValue = value.toDoubleOrNull() ?: 0.0
                    if (isTriglycerides) {
                        String.format("%.2f", convertMgDLToMmolLTriglycerides(mgdlValue))
                    } else {
                        String.format("%.2f", convertMgDLToMmolL(mgdlValue))
                    }
                } else {
                    value
                },
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                        val mgdlValue = if (unit == "mmol/L") {
                            if (isTriglycerides) {
                                convertMmolLToMgDLTriglycerides(newValue.toDoubleOrNull() ?: 0.0)
                            } else {
                                convertMmolLToMgDL(newValue.toDoubleOrNull() ?: 0.0)
                            }
                        } else {
                            newValue.toDoubleOrNull() ?: 0.0
                        }
                        onValueChange(String.format("%.2f", mgdlValue))
                    }
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.width(8.dp))
            UnitSpinner(
                currentUnit = unit,
                onUnitSelected = { newUnit ->
                    if (newUnit != unit) {
                        onUnitChange(newUnit)
                    }
                }
            )
        }
    }
}

@Composable
fun UnitSpinner(
    currentUnit: String,
    onUnitSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val units = listOf("mg/dL", "mmol/L")

    Box {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { expanded = true }
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                currentUnit,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = "Choose unit",
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            units.forEach { unit ->
                DropdownMenuItem(
                    text = { Text(unit) },
                    onClick = {
                        onUnitSelected(unit)
                        expanded = false
                    }
                )
            }
        }
    }
}
@Composable
fun ErrorMessage(message: String) {
    if (message.isNotEmpty()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

