# 🔐 Sistema de Autenticação JWT - Twitter Simplificado (em andamento)

Sistema de autenticação baseado em tokens JWT (JSON Web Token) para uma aplicação estilo Twitter, implementado com Spring Security.

## 📋 Como Funciona

### 🔑 POST /login - Autenticação

**Fluxo:**
1. Cliente envia `username` e `password`
2. Sistema valida credenciais no banco de dados
3. Senha é verificada usando BCrypt
4. Se válido, cria token JWT assinado com chave privada
5. Retorna token + tempo de expiração

**Request:**
```json
POST /login
{
  "username": "usuario",
  "password": "senha123"
}
```

**Response (Sucesso):**
```json
{
  "accessToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 300
}
```

**Response (Erro):**
```
401 Unauthorized - "user or password is invalid"
```

### 🛡️ GET /users - Rotas Protegidas

**Fluxo:**
1. Cliente envia token no header `Authorization`
2. Sistema valida token com chave pública
3. Verifica permissões (roles: ADMIN, BASIC)
4. Se OK, executa e retorna dados

**Request:**
```
GET /users
Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...
```

## 🔧 Componentes do Token JWT

O token contém:
- **issuer**: "mybackend" (quem emitiu)
- **subject**: ID do usuário
- **expiresAt**: Tempo atual + 300 segundos (5 minutos)
- **scope**: Roles do usuário (ex: "ADMIN BASIC")

## 🔒 Segurança

- ✅ Senhas criptografadas com BCrypt
- ✅ Token assinado com chave privada RSA
- ✅ Validação com chave pública
- ✅ Expiração automática (5 minutos)
- ✅ Controle de permissões por roles

## 🚀 Uso Prático

1. **Login**: Obtenha o token
2. **Guarde o token**: Armazene no cliente (localStorage, cookie, etc)
3. **Use em requisições**: Inclua no header `Authorization: Bearer <token>`
4. **Renove quando expirar**: Faça login novamente após 5 minutos
   
