# 🍽️ Rango na Régua

Aplicativo Android que monta o **ranking gastronômico** da turma: cada pessoa entra com a própria conta, indica lugares para comer perto do campus, vota e acompanha o ranking atualizado em tempo real.

Projeto da disciplina **Desenvolvimento de Aplicativos Móveis** (UniSENAI) — Semanas 5 (RecyclerView) e 9 (Firestore e Autenticação).

**Dupla:** _Nome 1_ e _Nome 2_

---

## ✨ Funcionalidades

- Cadastro, login e logout com **e-mail e senha** (Firebase Authentication)
- Sessão persistente: a abertura do app decide entre a tela de login e o ranking
- Indicar, **editar** e **excluir** lugares (a exclusão pede confirmação)
- Cada lugar é gravado com o **`uid` do dono**; a lista mostra só os lugares de quem está logado
- Ranking em **tempo real** (`addSnapshotListener`), com card de destaque para o líder
- Votação com `FieldValue.increment`, feita no servidor
- Alternância entre visualização em **lista** e **grade**
- **Filtro** por categoria
- Mensagens de erro de login e cadastro em português
- Regras de segurança do Firestore por dono (`read`, `create`, `update`, `delete`)

## 🧱 Tecnologias

| Item | Detalhe |
|---|---|
| Linguagem | Java |
| Interface | XML, Material Components, RecyclerView, ConstraintLayout |
| Backend | Firebase Authentication e Cloud Firestore |
| minSdk / compileSdk | 33 / 36 |
| Build | Gradle 9.1 e Android Gradle Plugin 9.0.1 |

## 🗂️ Estrutura

```
app/src/main/java/br/edu/unisenai/rangonaregua/
├── LoginActivity.java        # login e roteamento de sessão
├── CadastroActivity.java     # criação de conta
├── MainActivity.java         # ranking (lista/grade, filtro, sair)
├── NovoLugarActivity.java    # indicar e editar lugar
├── DetalheActivity.java      # detalhe, editar e excluir
├── Erros.java                # tradução dos erros do Firebase
├── adapter/LugarAdapter.java # RecyclerView com 2 tipos de item
├── data/LugarRepository.java # único acesso ao Firestore
├── data/Catalogo.java        # ordenação por votos
└── model/Lugar.java          # modelo do documento
firestore.rules               # regras de segurança por dono
```

---

## 🚀 Como rodar o projeto

### Pré-requisitos

- **Android Studio** recente (o projeto usa Gradle 9.1 e AGP 9.0.1)
- **SDK Android 16 (API 36)** instalado (Settings → Languages & Frameworks → Android SDK)
- Uma **conta Google** para acessar o Firebase
- Um dispositivo com **Android 13 (API 33) ou superior**:
  - emulador com imagem **Google APIs** ou **Google Play**, ou
  - celular físico com **Depuração USB** ativada

### 1. Clonar o repositório

```bash
git clone https://github.com/SEU-USUARIO/rango-na-regua.git
```

No Android Studio: **File → Open** e selecione a pasta do projeto. Aguarde o **Gradle Sync**.

> O arquivo `google-services.json` **não está no repositório** (está no `.gitignore`). Sem ele o app não compila. Siga os passos abaixo para gerar o seu.

### 2. Criar o projeto no Firebase

1. Acesse o [console do Firebase](https://console.firebase.google.com) e clique em **Criar um projeto**.
2. Dê um nome (ex.: `rango-na-regua`) e pode desligar o Google Analytics.

### 3. Registrar o app Android

1. Na visão geral do projeto, clique no ícone **Android**. Se não aparecer, vá em ⚙️ **Configurações do projeto → Geral → Seus apps → Adicionar app → Android**.
2. No **nome do pacote**, copie e cole exatamente:

   ```
   br.edu.unisenai.rangonaregua
   ```

   > Um erro de digitação aqui faz o app compilar, rodar e nunca conectar.
3. Clique em **Registrar app** e depois em **Fazer o download do google-services.json**.
4. Coloque o arquivo dentro da pasta **`app/`**, ao lado do `app/build.gradle`.

```
app/
├── build.gradle
├── google-services.json   ← aqui
└── src/
```

> No Android Studio, mude a visão do painel esquerdo de **Android** para **Project** para enxergar a pasta `app` real.

### 4. Ativar a autenticação

1. No console: **Build → Authentication → Vamos começar**.
2. Aba **Sign-in method** → **E-mail/senha**.
3. Ative a **primeira chave** e salve. Deixe **"Link do e-mail"** desligado.

### 5. Criar o banco Firestore

1. No console: **Build → Firestore Database → Criar banco de dados**.
2. Escolha a região **southamerica-east1 (São Paulo)**. Ela não pode ser trocada depois.
3. Comece em **modo de teste**.

### 6. Publicar as regras de segurança

1. No Firestore, abra a aba **Regras**.
2. Substitua o conteúdo pelo do arquivo [`firestore.rules`](firestore.rules):

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /lugares/{lugarId} {
      allow read: if request.auth != null
        && resource.data.uid == request.auth.uid;
      allow create: if request.auth != null
        && request.resource.data.uid == request.auth.uid;
      allow update, delete: if request.auth != null
        && resource.data.uid == request.auth.uid;
    }
  }
}
```

3. Clique em **Publicar**. Regras editadas e não publicadas continuam com o comportamento antigo.

### 7. Executar

1. **File → Sync Project with Gradle Files**.
2. Selecione o dispositivo na barra superior (campo ao lado do `app`).
3. Clique em **Run ▶**. O app abre na tela de login.

---

## 🧪 Como testar

1. Toque em **Criar uma conta**, informe e-mail e senha (mínimo 6 caracteres).
2. Toque no **+** e indique um lugar. Ele aparece sozinho no ranking, como líder.
3. Indique mais lugares e use **Votar** para ver o ranking reordenar.
4. Abra um lugar para **editar** ou **excluir**.
5. No menu ⋮, teste **Ver em grade**, **Filtrar categoria** e **Sair**.
6. Crie uma **segunda conta** e confirme que ela **não vê** os lugares da primeira.

### Contas de teste

| Conta | E-mail | Senha |
|---|---|---|
| 1 | _preencher_ | _preencher_ |
| 2 | _preencher_ | _preencher_ |

## 📸 Evidências

**Regras publicadas no Firebase**

![Regras publicadas](docs/regras.png)

**Coleção `lugares` no console**

![Coleção lugares](docs/colecao-lugares.png)

---

## 🛠️ Problemas comuns

| Sintoma | Causa provável |
|---|---|
| Erro de build citando `google-services.json` | O arquivo não está em `app/` |
| Cadastro falha com "operação não permitida" | Provedor E-mail/senha não ativado |
| Lista vazia ou "Falha ao carregar o ranking" | Firestore não criado, regras não publicadas ou pacote registrado com nome diferente |
| `PERMISSION_DENIED` no Logcat | Regras incorretas ou prazo do modo de teste vencido |
| Nada é gravado e não há erro | Sem internet no dispositivo, ou lugar gravado sem o campo `uid` |
| Sync do Gradle falha | Android Studio desatualizado ou SDK 36 não instalado |
| App não instala no emulador | Imagem abaixo do Android 13 (API 33) |
| Emulador travado ou lento | Reduza a imagem para API 34, aumente a RAM ou use um celular físico |

## 🔒 Segurança

- O `google-services.json` não deve ser versionado.
- A proteção dos dados vem das **regras do Firestore**, que só permitem acesso ao dono do documento. Nenhuma senha é guardada no banco.

## 📄 Licença

Projeto acadêmico, sem fins comerciais.
