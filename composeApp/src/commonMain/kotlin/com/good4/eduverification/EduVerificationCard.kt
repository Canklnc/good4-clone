package com.good4.eduverification

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.good4.core.presentation.ErrorRed
import com.good4.core.presentation.PrimaryGreen
import com.good4.core.presentation.SurfaceDefault
import com.good4.core.presentation.TextPrimary
import com.good4.core.presentation.TextSecondary

/** Shown on Askıda Yemek until the student proves a .edu.tr address. */
@Composable
fun EduVerificationCard(
    state: EduVerificationState,
    onOptInChange: (Boolean) -> Unit,
    onEmailChange: (String) -> Unit,
    onSendCode: () -> Unit,
    onCodeChange: (String) -> Unit,
    onConfirmCode: () -> Unit,
    onChangeEmail: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = PrimaryGreen,
        focusedLabelColor = PrimaryGreen,
        cursorColor = PrimaryGreen
    )
    val buttonColors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = Color.White)

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceDefault),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.School, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    "Askıda Yemek üniversite öğrencilerine özel",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            }
            Text(
                "Rezervasyon yapıp kod alabilmek için üniversite (.edu.tr) e-posta adresini doğrulaman gerekiyor.",
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = TextSecondary
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(role = Role.Checkbox, enabled = state.codeSentTo == null) { onOptInChange(!state.optedIn) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = state.optedIn,
                    onCheckedChange = null,
                    enabled = state.codeSentTo == null,
                    colors = CheckboxDefaults.colors(checkedColor = PrimaryGreen)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    "Edu mailimi aktif ederek Askıda Yemek'ten faydalanmak istiyorum",
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                    color = TextPrimary
                )
            }

            if (state.optedIn && state.codeSentTo == null) {
                OutlinedTextField(
                    value = state.email,
                    onValueChange = onEmailChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(".edu.tr e-posta adresin") },
                    placeholder = { Text("ad.soyad@ogr.akdeniz.edu.tr") },
                    singleLine = true,
                    enabled = !state.isSending,
                    colors = fieldColors,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { onSendCode() })
                )
                Button(
                    onClick = onSendCode,
                    enabled = !state.isSending && state.email.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = buttonColors,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (state.isSending) {
                        CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text("Doğrulama kodu gönder", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            state.codeSentTo?.let { sentTo ->
                Text(
                    "6 haneli kod $sentTo adresine gönderildi. Gelen kutunda yoksa gereksiz (spam) klasörüne de bak.",
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = TextSecondary
                )
                OutlinedTextField(
                    value = state.code,
                    onValueChange = onCodeChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Doğrulama kodu") },
                    singleLine = true,
                    enabled = !state.isConfirming,
                    colors = fieldColors,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { onConfirmCode() })
                )
                Button(
                    onClick = onConfirmCode,
                    enabled = !state.isConfirming && state.code.length == 6,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = buttonColors,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (state.isConfirming) {
                        CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text("Doğrula", fontWeight = FontWeight.SemiBold)
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton(onClick = onChangeEmail, enabled = !state.isConfirming) {
                        Text("E-postayı değiştir", color = PrimaryGreen)
                    }
                    TextButton(onClick = onSendCode, enabled = state.resendSeconds == 0 && !state.isSending) {
                        Text(
                            if (state.resendSeconds > 0) "Tekrar gönder (${state.resendSeconds} sn)" else "Kodu tekrar gönder",
                            color = if (state.resendSeconds > 0) TextSecondary else PrimaryGreen
                        )
                    }
                }
            }

            state.errorMessage?.let {
                Text(it, fontSize = 13.sp, color = ErrorRed)
            }
        }
    }
}

@Composable
fun EduVerifiedBadge(email: String?, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Outlined.Verified, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text(
            if (email.isNullOrBlank()) "Üniversite e-postan doğrulandı" else "$email doğrulandı",
            fontSize = 12.sp,
            color = TextSecondary
        )
    }
}
