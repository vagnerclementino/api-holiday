# 🤝 Guia de Contribuição

Obrigado por considerar contribuir com o **Data-Oriented Programming Holiday API**! São pessoas como você que tornam este projeto uma excelente ferramenta de aprendizado.

Seguir estas diretrizes ajuda a comunicar que você respeita o tempo dos desenvolvedores que mantêm este projeto open source. Em troca, eles devem retribuir esse respeito ao abordar sua issue, avaliar mudanças e ajudá-lo a finalizar seus pull requests.

## 🎯 Tipos de Contribuições Bem-vindas

Mantemos uma mente aberta! Existem muitas maneiras de contribuir:

- **📚 Documentação**: Melhorar documentação, escrever tutoriais ou posts
- **🐛 Relatórios de Bug**: Identificar e reportar problemas
- **✨ Novas Funcionalidades**: Sugerir e implementar melhorias
- **🧪 Testes**: Adicionar ou melhorar testes existentes
- **🎨 Exemplos DOP**: Criar novos exemplos de Programação Orientada a Dados
- **🔧 Refatoração**: Melhorar código existente seguindo princípios DOP

## 🚫 Contribuições NÃO Aceitas

- Issues de suporte técnico (use as Discussions do GitHub)
- Mudanças que quebrem os princípios de Data-Oriented Programming
- Código malicioso ou com vulnerabilidades de segurança
- Alterações que não sigam os padrões de qualidade do projeto

## 📋 Regras Básicas

### Responsabilidades Técnicas
- **✅ Compatibilidade**: Garantir compatibilidade com Java 24 e Spring Boot 3.5.4
- **🧪 Testes**: Todo código deve ter testes unitários e de integração
- **📐 Padrões DOP**: Seguir os 4 princípios de Data-Oriented Programming v1.1
- **🎨 Estilo**: Manter consistência com Checkstyle e formatação existente
- **📝 Documentação**: Documentar mudanças significativas

### Responsabilidades de Comportamento
- Seja respeitoso e construtivo em todas as interações
- Crie issues para mudanças significativas antes de implementar
- Mantenha PRs pequenos e focados em uma funcionalidade
- Seja receptivo a feedback e sugestões de melhoria

## 🚀 Sua Primeira Contribuição

Não sabe por onde começar? Procure por issues com as labels:
- **`good first issue`**: Issues adequadas para iniciantes
- **`help wanted`**: Issues que precisam de ajuda da comunidade
- **`documentation`**: Melhorias na documentação

**Primeira vez contribuindo para open source?** Confira estes recursos:
- [Como Contribuir para um Projeto Open Source no GitHub](https://egghead.io/series/how-to-contribute-to-an-open-source-project-on-github)
- [First Timers Only](http://www.firsttimersonly.com/)

## 🛠️ Como Contribuir

### Para Mudanças Pequenas (correções óbvias)
Correções pequenas como erros de digitação, formatação ou comentários podem ser enviadas diretamente via PR:

1. Fork o repositório
2. Faça a correção
3. Envie um pull request

### Para Mudanças Significativas

1. **📝 Crie uma Issue**: Descreva a mudança proposta
2. **🍴 Fork o Repositório**: Crie sua própria cópia
3. **🌿 Crie uma Branch**: `git checkout -b feature/minha-funcionalidade`
4. **💻 Desenvolva**: Implemente sua mudança seguindo os padrões
5. **🧪 Teste**: Execute todos os testes localmente
6. **📋 Qualidade**: Verifique style checks e formatação
7. **📤 Pull Request**: Envie seu PR com descrição detalhada

### Comandos de Desenvolvimento

```bash
# Setup completo do ambiente
make setup

# Executar testes
make test-all

# Verificar qualidade do código
make quality

# Verificar estilo
make style-check

# Corrigir formatação
make format-fix
```

## 🧪 Guia de Testes

O projeto utiliza uma estratégia de testes categorizada com **JUnit 5 tags** para separar testes unitários e de integração.

### Categorias de Testes

#### 🔵 Testes Unitários (`@Tag("unit")`)
Testes rápidos e isolados que não requerem dependências externas:

**Características:**
- ⚡ Execução rápida (< 1 segundo por teste)
- 🔒 Isolados (sem dependências externas)
- 🎯 Focados (testam unidades individuais)
- 🔄 Repetíveis (mesmo resultado sempre)

**Exemplos:**
- Testes de objetos de domínio (Holiday, Location)
- Validação de DTOs
- Testes de mappers com dados mock
- Testes de classes utilitárias

#### 🟢 Testes de Integração (`@Tag("integration")`)
Verificam que diferentes componentes funcionam juntos corretamente:

**Características:**
- 🐳 Usa TestContainers para instâncias reais do MongoDB
- 🌐 Testa contexto completo da aplicação
- 📊 Verifica funcionalidade end-to-end
- ⏱️ Execução mais lenta (vários segundos por teste)

**Exemplos:**
- Testes de carregamento do contexto Spring Boot
- Testes de integração com banco de dados
- Testes de endpoints da API
- Testes de integração da camada de serviço

### Executando Testes

#### Todos os Testes (Padrão)
```bash
# Executar todos os testes (unitários + integração)
./mvnw test

# Ou explicitamente usar o profile all-tests
./mvnw test -Pall-tests
```

#### Apenas Testes Unitários
```bash
# Execução rápida - apenas testes unitários
./mvnw test -Punit-tests
```

#### Apenas Testes de Integração
```bash
# Execução mais lenta - apenas testes de integração
./mvnw test -Pintegration-tests
```

#### Testes Específicos
```bash
# Executar uma classe de teste específica
./mvnw test -Dtest=HolidayOperationsTest

# Executar múltiplas classes de teste
./mvnw test -Dtest=HolidayOperationsTest,CreateHolidayRequestTest
```

### Adicionando Novos Testes

#### Para Testes Unitários
```java
@Tag("unit")
class MyServiceTest {
    
    @Test
    void shouldDoSomething() {
        // Implementação do teste
    }
}
```

#### Para Testes de Integração
```java
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@Tag("integration")
class MyIntegrationTest {
    
    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:8");
    
    @Test
    void shouldIntegrateCorrectly() {
        // Implementação do teste de integração
    }
}
```

### Boas Práticas de Testes

#### Testes Unitários
- ✅ Use anotação `@Tag("unit")`
- ✅ Mock dependências externas
- ✅ Teste unidades individuais de funcionalidade
- ✅ Mantenha testes rápidos (< 1 segundo)
- ✅ Use nomes descritivos para testes
- ✅ Siga o padrão AAA (Arrange, Act, Assert)

#### Testes de Integração
- ✅ Use anotação `@Tag("integration")`
- ✅ Use TestContainers para bancos de dados reais
- ✅ Teste interações entre componentes
- ✅ Use `@SpringBootTest` para contexto completo
- ✅ Limpe recursos após os testes
- ✅ Use dados de teste realistas

#### Diretrizes Gerais
- 📝 Escreva testes antes ou junto com o código (TDD/BDD)
- 🎯 Busque alta cobertura de testes (>80%)
- 🔄 Mantenha testes independentes e repetíveis
- 📚 documente cenários de teste complexos
- 🚀 Execute testes frequentemente durante desenvolvimento

## 📮 Testes com Postman

O projeto inclui **coleções Postman abrangentes** para testar todos os endpoints da API e padrões DOP.

### 🚀 Início Rápido com Postman

#### 1. Importar Coleções
1. Abra o Postman
2. Clique em **Import**
3. Selecione `postman/DOP-Holiday-API.postman_collection.json`
4. Selecione `postman/DOP-Holiday-API.postman_environment.json`

#### 2. Configurar Ambiente
1. Selecione **🎯 DOP Holiday API - Master Environment** no dropdown de ambiente
2. Certifique-se que a API está rodando em `http://localhost:8080`
3. Atualize variáveis se necessário (porta diferente, URL, etc.)

#### 3. Executar Testes
- **Testes Individuais**: Clique em qualquer request e pressione **Send**
- **Testes por Categoria**: Clique com botão direito em uma pasta e selecione **Run folder**
- **Suite Completa**: Clique na coleção e selecione **Run collection**

### 📊 Estrutura da Coleção

A coleção está organizada em **5 categorias principais** com **23 testes abrangentes**:

#### 🟢 1. Operações CRUD Básicas (13 requests)
Operações padrão de Create, Read, Update, Delete para feriados
- Criar feriados
- Recuperar feriados por ID
- Atualizar feriados existentes
- Deletar feriados
- Listar todos os feriados

#### 🔵 2. Filtragem e Recuperação Avançada (1 request)
Consultas complexas, filtragem e padrões de recuperação de dados
- Filtrar por país, estado, cidade
- Filtrar por tipo de feriado
- Filtragem por intervalo de datas
- Correspondência de padrões de nome

#### 🟡 3. Validação e Tratamento de Erros (4 requests)
Validação de entrada, cenários de erro e condições de contorno
- Validação de dados inválidos
- Validação de campos obrigatórios
- Validação de datas
- Teste de respostas de erro

#### 🟠 4. Tipos Específicos DOP (4 subcategorias)
Tipos de feriados específicos de Data-Oriented Programming

##### 📅 Feriados Fixos (2 requests)
Feriados de data fixa (Natal, Ano Novo, etc.)

##### 👁️ Feriados Observados (1 request)
Feriados com datas observadas e regras de segunda-feira

##### 🔄 Feriados Móveis (1 request)
Feriados calculados (Páscoa, Ação de Graças, etc.)

##### 🔗 Feriados Móveis Baseados (1 request)
Feriados calculados a partir de outros feriados

#### 🔴 5. Testes de Performance (1 request)
Testes de performance, operações em lote e testes de stress

### 🌍 Variáveis de Ambiente

| Variável | Valor Padrão | Descrição |
|----------|--------------|-----------|
| `baseUrl` | `http://localhost:8080` | URL base da Holiday API |
| `apiVersion` | `v1` | Versão da API |
| `contentType` | `application/json` | Tipo de conteúdo padrão |
| `testCountry` | `BR` | País padrão para testes |
| `testState` | `SP` | Estado padrão para testes |
| `testCity` | `São Paulo` | Cidade padrão para testes |
| `currentYear` | `2024` | Ano atual para testes |
| `timeout` | `5000` | Timeout de request em milissegundos |

### 📈 Ordem de Execução dos Testes

Para melhores resultados, execute os testes nesta ordem:

1. **🟢 Operações CRUD Básicas** - Estabelecer funcionalidade base
2. **🟠 Tipos Específicos DOP** - Testar funcionalidades específicas do domínio
3. **🔵 Filtragem Avançada** - Testar capacidades de consulta
4. **🟡 Validação e Tratamento de Erros** - Testar casos extremos
5. **🔴 Testes de Performance** - Testar performance do sistema

### 🎯 Melhores Práticas para Postman

1. **Sempre use a coleção principal** para novos testes
2. **Execute a suite completa** antes de releases importantes
3. **Atualize variáveis de ambiente** para diferentes ambientes
4. **Adicione novos testes** às categorias apropriadas
5. **Use nomes descritivos** para novos requests
6. **Inclua asserções de teste** para todos os requests
7. **Documente cenários complexos** nas descrições dos requests

## 🐛 Como Reportar Bugs

### ⚠️ Vulnerabilidades de Segurança
**NÃO abra uma issue pública para vulnerabilidades de segurança.** Entre em contato diretamente via email.

### 🐞 Bugs Gerais
Ao reportar um bug, inclua:

1. **Versão do Java**: Qual versão você está usando?
2. **Sistema Operacional**: Windows, macOS, Linux?
3. **O que você fez**: Passos para reproduzir
4. **O que esperava**: Comportamento esperado
5. **O que aconteceu**: Comportamento atual
6. **Logs/Erros**: Inclua mensagens de erro relevantes

## ✨ Como Sugerir Funcionalidades

### Filosofia do Projeto
Este projeto demonstra **Data-Oriented Programming v1.1** em Java, focando em:
- Dados imutáveis e transparentes
- Separação entre dados e operações
- Estados ilegais impossíveis de representar
- Modelagem precisa do domínio

### Processo de Sugestão
1. **🔍 Verifique**: Se a funcionalidade já foi sugerida
2. **📝 Crie Issue**: Use o template de feature request
3. **🎯 Descreva**: Por que é necessária e como deveria funcionar
4. **🏗️ Considere**: Como se alinha com os princípios DOP

## 🔍 Processo de Code Review

### Workflow de Qualidade Obrigatório
Todos os PRs devem passar pelo **workflow de qualidade automatizado**:

- **🏗️ Build Application**: Compilação bem-sucedida
- **🎨 Code Style Check**: Verificação de estilo com Checkstyle
- **🧪 Unit Tests**: Todos os testes unitários passando
- **🔗 Integration Tests**: Testes de integração com MongoDB
- **🚪 Quality Gate**: Agregação de todos os resultados

### Proteção de Branch
A branch `main` está protegida e requer:
- ✅ Todos os status checks passando
- ✅ Branch atualizada antes do merge
- ✅ Aprovação de code review
- ✅ Histórico linear (recomendado)

### Cronograma de Review
- **Reviews iniciais**: Dentro de 2-3 dias úteis
- **Feedback esperado**: Resposta em até 1 semana
- **PRs inativos**: Podem ser fechados após 2 semanas sem atividade

## 🏛️ Convenções do Projeto

### Estilo de Código
- **Java 24**: Use preview features quando apropriado
- **Records**: Para todos os objetos de domínio
- **Pattern Matching**: Para operações com sealed interfaces
- **Checkstyle**: Configuração em `checkstyle.xml`
- **Spotless**: Formatação automática configurada

### Mensagens de Commit
Use o formato:
```
tipo(escopo): descrição breve

Descrição mais detalhada se necessário.

Fixes #123
```

Tipos: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`

### Labels de Issues
- **`bug`**: Problemas confirmados
- **`enhancement`**: Novas funcionalidades
- **`documentation`**: Melhorias na documentação
- **`good first issue`**: Adequado para iniciantes
- **`help wanted`**: Precisa de ajuda da comunidade
- **`dop-example`**: Novos exemplos de DOP

## 🌟 Reconhecimento

Contribuidores são reconhecidos:
- **README.md**: Lista de contribuidores
- **Releases**: Menção em notas de release
- **Issues**: Crédito em issues relacionadas

## 📞 Comunidade

- **GitHub Discussions**: Para discussões gerais
- **Issues**: Para bugs e feature requests
- **Pull Requests**: Para contribuições de código

---

**Lembre-se**: Este é um projeto educacional focado em demonstrar Data-Oriented Programming. Toda contribuição deve alinhar-se com este objetivo pedagógico.
