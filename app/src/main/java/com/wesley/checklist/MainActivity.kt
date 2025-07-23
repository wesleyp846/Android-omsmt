package com.wesley.checklist

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wesley.checklist.ui.theme.OMSMTTheme

data class Pessoa(
    var nome: String = "",
    var matricula: String = ""
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OMSMTTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DailySectorExitForm()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailySectorExitForm() {
    val context = LocalContext.current

    // Estados para os campos do formulário
    var pessoas by remember { mutableStateOf(listOf(Pessoa())) }
    var telefone by remember { mutableStateOf("") }
    var carro by remember { mutableStateOf("") }
    var ordem by remember { mutableStateOf("") }
    var horarioChave by remember { mutableStateOf("") }
    var saida by remember { mutableStateOf("") }
    var motivoAtraso by remember { mutableStateOf("N/A") }

    // Estados para validação
    var telefoneError by remember { mutableStateOf(false) }

    // Estados para Time Pickers
    var showTimePickerChave by remember { mutableStateOf(false) }
    var showTimePickerSaida by remember { mutableStateOf(false) }
    val timePickerStateChave = rememberTimePickerState()
    val timePickerStateSaida = rememberTimePickerState()
    // Adicione este estado de erro antes do Column:
    var carroError by remember { mutableStateOf(false) }
    var ordemError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Título
        Text(
            text = "SAÍDA DIÁRIA DO SETOR",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Seção de Pessoas (Nome e Matrícula dinâmicos)
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ELETRICISTAS:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row {
                        // Botão para adicionar pessoa
                        IconButton(
                            onClick = { pessoas = pessoas + Pessoa() }
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Adicionar pessoa",
                                tint = Color(0xFF4CAF50)
                            )
                        }

                        // Botão para remover pessoa (só aparece se houver mais de uma)
                        if (pessoas.size > 1) {
                            IconButton(
                                onClick = { pessoas = pessoas.dropLast(1) }
                            ) {
                                Icon(
                                    Icons.Default.Remove,
                                    contentDescription = "Remover pessoa",
                                    tint = Color(0xFFF44336)
                                )
                            }
                        }
                    }
                }

                // Campos dinâmicos para cada pessoa
                pessoas.forEachIndexed { index, pessoa ->
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (pessoas.size > 1) {
                            Text(
                                text = "Pessoa ${index + 1}:",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Nome
                        OutlinedTextField(
                            value = pessoa.nome,
                            onValueChange = {
                                pessoas = pessoas.toMutableList().apply {
                                    this[index] = this[index].copy(nome = it.uppercase())
                                }
                            },
                            placeholder = { Text("Ex: Seu Nome") },
                            label = { Text("Nome") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Matrícula
                        OutlinedTextField(
                            value = pessoa.matricula,
                            onValueChange = {
                                // Garante apenas digitos
                                val filtered = it.filter { char -> char.isDigit() }
                                // Garante apenas 7 digitos
                                if (filtered.length <= 7)
                                pessoas = pessoas.toMutableList().apply {
                                    this[index] = this[index].copy(matricula = filtered)
                                }
                            },
                            placeholder = { Text("Ex: 0000000") },
                            label = { Text("Matrícula") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            supportingText = { Text("7 dígitos numéricos") }
                        )

                        if (index < pessoas.size - 1) {
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                        }
                    }
                }
            }
        }

        // TELEFONE
        Text(
            text = "TELEFONE:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        OutlinedTextField(
            value = telefone,
            onValueChange = {
                // Limita a 2 dígitos e apenas números
                val filtered = it.filter { char -> char.isDigit() }
                if (filtered.length <= 2) {
                    telefone = filtered
                    telefoneError = false
                } else {
                    telefoneError = true
                }
            },
            placeholder = { Text("Ex: 00") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = telefoneError,
            supportingText = {
                if (telefoneError) {
                    Text("Apenas 2 dígitos são permitidos", color = MaterialTheme.colorScheme.error)
                } else {
                    Text("Digite apenas 2 dígitos")
                }
            }
        )

        // Carro
        Text(
            text = "Placa do carro:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        OutlinedTextField(
            value = carro,
            onValueChange = {
                val filtered = it.filter { char -> char.isLetterOrDigit() }.uppercase()
                if (validarFormatoCarroTemporario(filtered) && filtered.length <= 7) {
                    carro = filtered
                    carroError = false
                } else if (filtered.length > 7 || !validarFormatoCarroTemporario(filtered)) {
                    carroError = true
                }
            },
            placeholder = { Text("Ex: TUU0H16") },
            modifier = Modifier.fillMaxWidth(),
            isError = carroError,
            supportingText = {
                if (carroError) {
                    Text("Formato inválido (AAA#A##)", color = MaterialTheme.colorScheme.error)
                } else {
                    Text("Formato: AAA#A## (ex: TUU0H16)")
                }
            }
        )

        // ORDEM
        Text(
            text = "ORDEM:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        OutlinedTextField(
            value = ordem,
            onValueChange = {
                val filtered = it.filter { char -> char.isDigit() }
                if (filtered.length <= 5) {
                    ordem = filtered
                    ordemError = false
                } else {
                    ordemError = true
                }
            },
            placeholder = { Text("Ex: 45329") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = ordemError,
            supportingText = {
                if (ordemError) {
                    Text("Apenas 5 dígitos numéricos", color = MaterialTheme.colorScheme.error)
                } else {
                    Text("Digite 5 dígitos numéricos")
                }
            }
        )

        // Horário de retirada da chave no almoxarifado
        Text(
            text = "Horário de retirada da chave no almoxarifado:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        OutlinedTextField(
            value = horarioChave,
            onValueChange = { },
            placeholder = { Text("Toque no ícone do relógio") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showTimePickerChave = true }) {
                    Icon(Icons.Default.AccessTime, contentDescription = "Selecionar horário")
                }
            },
            supportingText = { Text("Toque no ícone do relógio para selecionar o horário") }
        )

        // Saída
        Text(
            text = "Saída da base:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        OutlinedTextField(
            value = saida,
            onValueChange = { },
            placeholder = { Text("Toque no ícone do relógio") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showTimePickerSaida = true }) {
                    Icon(Icons.Default.AccessTime, contentDescription = "Selecionar horário")
                }
            },
            supportingText = { Text("Toque no ícone do relógio para selecionar o horário") }
        )

        // MOTIVO DE ATRASO
        Text(
            text = "MOTIVO DE ATRASO:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        OutlinedTextField(
            value = motivoAtraso,
            onValueChange = { motivoAtraso = it.uppercase() },
            placeholder = { Text("Ex: Justificativa se houver atraso") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            supportingText = { Text("Descreva o motivo do atraso, se houver") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botão Enviar via WhatsApp
        Button(
            onClick = {
                if (validarCamposCompletos(pessoas, telefone, carro, ordem, horarioChave, saida, motivoAtraso, context)) {
                    val mensagem = formatarMensagem(pessoas, telefone, carro, ordem, horarioChave, saida, motivoAtraso)
                    enviarViaWhatsApp(mensagem, context)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF25D366) // Cor verde do WhatsApp
            )
        ) {
            Text(
                text = "📱 Enviar via WhatsApp",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Botão alternativo para compartilhar
        Button(
            onClick = {
                if (validarCamposCompletos(pessoas, telefone, carro, ordem, horarioChave, saida, motivoAtraso, context)) {
                    val mensagem = formatarMensagem(pessoas, telefone, carro, ordem, horarioChave, saida, motivoAtraso)
                    compartilharTexto(mensagem, context)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2196F3) // Cor azul
            )
        ) {
            Text(
                text = "📤 Compartilhar",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }

    // Time Picker Dialog para Horário da Chave
    if (showTimePickerChave) {
        TimePickerDialog(
            onDismiss = { showTimePickerChave = false },
            onConfirm = {
                horarioChave = String.format("%02d:%02d", timePickerStateChave.hour, timePickerStateChave.minute)
                showTimePickerChave = false
            }
        ) {
            TimePicker(state = timePickerStateChave)
        }
    }

    // Time Picker Dialog para Horário de Saída
    if (showTimePickerSaida) {
        TimePickerDialog(
            onDismiss = { showTimePickerSaida = false },
            onConfirm = {
                saida = String.format("%02d:%02d", timePickerStateSaida.hour, timePickerStateSaida.minute)
                showTimePickerSaida = false
            }
        ) {
            TimePicker(state = timePickerStateSaida)
        }
    }
}

@Composable
fun TimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("OK")
            }
        },
        text = {
            content()
        }
    )
}

fun validarCamposCompletos(
    pessoas: List<Pessoa>,
    telefone: String,
    carro: String,
    ordem: String,
    horarioChave: String,
    saida: String,
    motivoAtraso: String,
    context: android.content.Context
): Boolean {
    // Validar pessoas
    pessoas.forEachIndexed { index, pessoa ->
        if (pessoa.nome.trim().isEmpty()) {
            Toast.makeText(context, "Por favor, preencha o nome da pessoa ${index + 1}", Toast.LENGTH_SHORT).show()
            return false
        }
        // Validação de Matrícula (7 dígitos numéricos)
        if (pessoa.matricula.length != 7 || !pessoa.matricula.all { it.isDigit() }) {
            Toast.makeText(context, "Matrícula da pessoa ${index + 1} deve conter exatamente 7 dígitos numéricos", Toast.LENGTH_SHORT).show()
            return false
        }
    }
    // Validação específica do telefone (2 dígitos numéricos)
    if (telefone.length != 2 || !telefone.all { it.isDigit() }) {
        Toast.makeText(context, "TELEFONE deve conter exatamente 2 dígitos numéricos", Toast.LENGTH_SHORT).show()
        return false
    }

// Validação do Carro (3 letras + 1 número + 1 letra + 2 números)
    val regexCarro = Regex("^[A-Z]{3}[0-9][A-Z][0-9]{2}$")
    if (!carro.matches(regexCarro)) {
        Toast.makeText(context, "Carro deve estar no formato AAA#A## (ex: TUU0H16)", Toast.LENGTH_SHORT).show()
        return false
    }

// Validação da Ordem (5 dígitos numéricos)
    if (ordem.length != 5 || !ordem.all { it.isDigit() }) {
        Toast.makeText(context, "ORDEM deve conter exatamente 5 dígitos numéricos", Toast.LENGTH_SHORT).show()
        return false
    }

    val campos = listOf(
        telefone to "TELEFONE",
        carro to "Carro",
        ordem to "ORDEM",
        horarioChave to "Horário de retirada da chave no almoxarifado",
        saida to "Saída",
        motivoAtraso to "MOTIVO DE ATRASO"
    )

    for ((campo, nomeCampo) in campos) {
        if (campo.trim().isEmpty()) {
            Toast.makeText(context, "Por favor, preencha o campo: $nomeCampo", Toast.LENGTH_SHORT).show()
            return false
        }
    }

    // Validação específica do telefone
    if (telefone.length != 2 || !telefone.all { it.isDigit() }) {
        Toast.makeText(context, "TELEFONE deve conter exatamente 2 dígitos", Toast.LENGTH_SHORT).show()
        return false
    }

    return true
}

fun formatarMensagem(
    pessoas: List<Pessoa>,
    telefone: String,
    carro: String,
    ordem: String,
    horarioChave: String,
    saida: String,
    motivoAtraso: String
): String {
    val nomes = pessoas.joinToString(" e ") { it.nome }
    val matriculas = pessoas.joinToString(" e ") { it.matricula }

    return """
BOA NOITE

SAIDA DIÁRIA DO SETOR

NOME: $nomes
Matrícula: $matriculas
TELEFONE: $telefone
Carro: $carro
ORDEM: $ordem
Horário de retirada da chave no almoxarifado: $horarioChave
Saída: $saida
MOTIVO DE ATRASO: $motivoAtraso
    """.trimIndent()
}

fun enviarViaWhatsApp(mensagem: String, context: android.content.Context) {
    try {
        // Tenta primeiro com o pacote específico do WhatsApp
        val whatsappIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            setPackage("com.whatsapp")
            putExtra(Intent.EXTRA_TEXT, mensagem)
        }

        if (whatsappIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(whatsappIntent)
            return
        }

        // Se não funcionar, tenta com a URL do WhatsApp
        val urlIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://api.whatsapp.com/send?text=${Uri.encode(mensagem)}")
        }

        if (urlIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(urlIntent)
            return
        }

        // Se nada funcionar, usa o compartilhamento genérico
        compartilharTexto(mensagem, context)

    } catch (e: Exception) {
        Toast.makeText(context, "Erro ao abrir WhatsApp. Usando compartilhamento alternativo.", Toast.LENGTH_SHORT).show()
        compartilharTexto(mensagem, context)
    }
}

fun compartilharTexto(mensagem: String, context: android.content.Context) {
    try {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, mensagem)
            putExtra(Intent.EXTRA_SUBJECT, "Padrão de Saída Diária do Setor")
        }

        val chooser = Intent.createChooser(shareIntent, "Compartilhar via:")
        context.startActivity(chooser)

    } catch (e: Exception) {
        Toast.makeText(context, "Erro ao compartilhar: ${e.message}", Toast.LENGTH_LONG).show()
    }
}

fun validarFormatoCarroTemporario(texto: String): Boolean {
    // Permite digitação gradual seguindo o padrão AAA#A##
    val regexes = listOf(
        "^$",                           // Vazio
        "^[A-Z]$",                      // 1 letra
        "^[A-Z]{2}$",                   // 2 letras
        "^[A-Z]{3}$",                   // 3 letras
        "^[A-Z]{3}[0-9]$",             // 3 letras + 1 número
        "^[A-Z]{3}[0-9][A-Z]$",        // 3 letras + 1 número + 1 letra
        "^[A-Z]{3}[0-9][A-Z][0-9]$",   // 3 letras + 1 número + 1 letra + 1 número
        "^[A-Z]{3}[0-9][A-Z][0-9]{2}$" // Formato completo
    )
    return regexes.any { texto.matches(Regex(it)) }
}
