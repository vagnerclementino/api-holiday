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
