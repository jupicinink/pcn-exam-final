# COVID-19 Isolation Index System

Sistema desenvolvido como avaliação final da disciplina de **Programação para Camada de Negócio** do curso de Engenharia de Software / Ciência da Computação da **Unijuí**, com o objetivo de importar, processar, armazenar e exportar índices de isolamento social de cidades brasileiras durante a pandemia de COVID-19.

---

## 👩‍💻 Autoras

- Júlia da Silva Picinini
- Stéfani Gabriele Arnold de Camargo

---

## 📋 Sobre o projeto

A aplicação parte de arquivos CSV contendo índices de isolamento social por cidade e estado brasileiros. A partir desses dados, o sistema permite:

- Carregar configurações de banco de dados a partir de um arquivo XML
- Importar os dados dos CSVs para um banco de dados Java DB (Derby) usando threads
- Consultar os maiores e menores índices de isolamento por estado ou para todo o Brasil
- Exportar os dados em formato XML
- Registrar logs de execução configuráveis

---

## 🏗️ Arquitetura

O projeto segue a separação em camadas:

- **Camada de Interface (GUI):** `MainFrame.java` — interface gráfica Swing
- **Camada de Negócio (Logic):** `DBManager.java`, `IsolationCSVImporter.java`, `IsolationRecord.java`, `XMLTransformer.java`
- **Utilitários:** `XMLHandler.java`

---

## ⚙️ Tecnologias utilizadas

- Java (NetBeans)
- Java DB (Apache Derby)
- JDBC para acesso ao banco de dados
- Java Swing para interface gráfica
- XPath para leitura de XML
- Threads / ExecutorService para processamento paralelo
- Sistema de log configurável

---

## 🗄️ Banco de dados

O sistema utiliza duas tabelas:

**STATE**
| Campo | Tipo |
|-------|------|
| ID | INTEGER (PK) |
| NAME | VARCHAR |
| ACRONYM | VARCHAR |

**SOCIAL_ISOLATION**
| Campo | Tipo |
|-------|------|
| ID | INTEGER (PK) |
| CITY | VARCHAR |
| STATE_ID | INTEGER (FK) |
| INDEX | DOUBLE |
| DATE_WHEN | VARCHAR |

---

## 🚀 Como executar

1. Clone o repositório
2. Abra o projeto no NetBeans
3. No painel **Services**, conecte ao Java DB e execute o script `create-database-script.txt` para criar as tabelas
4. Verifique o arquivo `config.xml` na raiz do projeto e ajuste as configurações de conexão se necessário
5. Execute o projeto pela classe principal `MainFrame.java`
6. Na interface, clique em **Load XML Conf** para carregar as configurações
7. Clique em **Select the CSV files** para selecionar os arquivos da pasta `social-distancing-cities-by-state`
8. Clique em **Insert the records in the database** para importar os dados
9. Use **Search** para consultar os índices de isolamento
10. Use **Export to XML** para exportar os dados

---

## 📁 Estrutura do projeto

```
pcn-exam-final/
├── src/
│   └── br/edu/unijui/
│       ├── gui/
│       │   └── MainFrame.java
│       └── pcn/
│           ├── logic/
│           │   ├── DBManager.java
│           │   ├── IsolationCSVImporter.java
│           │   ├── IsolationRecord.java
│           │   └── XMLTransformer.java
│           └── utils/
│               └── XMLHandler.java
├── lib/
│   └── derbyclient.jar
├── social-distancing-cities-by-state/
├── config.xml
├── create-database-script.txt
└── export-format.xml
```

---

## 📄 Licença

Projeto acadêmico — Unijuí, 2026.
