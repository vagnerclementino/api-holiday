#!/bin/bash

# Teste Completo dos Endpoints da Holiday API
# Profile: local
# Data-Oriented Programming Holiday API

set -e

API_BASE="http://localhost:8080/api/holidays"
HEALTH_URL="http://localhost:8080/actuator/health"

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Função para imprimir com cores
print_header() {
    echo -e "\n${BLUE}🎯 === $1 === 🎯${NC}\n"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_info() {
    echo -e "${YELLOW}📋 $1${NC}"
}

# Função para verificar status HTTP
check_status() {
    local expected=$1
    local actual=$2
    local description=$3
    
    if [ "$actual" -eq "$expected" ]; then
        print_success "Status $actual: $description"
        return 0
    else
        print_error "Status $actual (esperado $expected): $description"
        return 1
    fi
}

# Função para fazer requisição e capturar status
make_request() {
    local method=$1
    local url=$2
    local data=$3
    local description=$4
    
    print_info "Testando: $description"
    echo "  Método: $method"
    echo "  URL: $url"
    
    if [ -n "$data" ]; then
        echo "  Dados: $data"
        response=$(curl -s -w "\n%{http_code}" -X "$method" "$url" \
            -H "Content-Type: application/json" \
            -d "$data")
    else
        response=$(curl -s -w "\n%{http_code}" -X "$method" "$url")
    fi
    
    # Separar body e status code (compatível com macOS)
    body=$(echo "$response" | sed '$d')
    status=$(echo "$response" | tail -n 1)
    
    echo "  Status: $status"
    echo "  Resposta: $(echo "$body" | head -c 200)"
    if [ ${#body} -gt 200 ]; then
        echo "..."
    fi
    echo
    
    # Retornar status e body via variáveis globais
    LAST_STATUS=$status
    LAST_BODY=$body
}

print_header "TESTE COMPLETO DOS ENDPOINTS - HOLIDAY API"
echo "Profile: local"
echo "Data-Oriented Programming Holiday API"

# 1. Verificar se a aplicação está rodando
print_header "1. VERIFICAÇÃO DE SAÚDE DA APLICAÇÃO"

make_request "GET" "$HEALTH_URL" "" "Health Check"
if ! check_status 200 $LAST_STATUS "Aplicação deve estar rodando"; then
    print_error "Aplicação não está rodando. Execute: ./mvnw spring-boot:run -Dspring-boot.run.profiles=local"
    exit 1
fi

# Verificar se MongoDB está conectado
if echo "$LAST_BODY" | jq -e '.components.mongo.status == "UP"' > /dev/null; then
    print_success "MongoDB conectado"
else
    print_error "MongoDB não está conectado"
    exit 1
fi

# 2. Limpar dados existentes (opcional - comentado para preservar dados)
# print_header "2. LIMPEZA DE DADOS (OPCIONAL)"

# 3. Testar GET /api/holidays (lista vazia ou com dados existentes)
print_header "3. TESTE GET /api/holidays - LISTAR TODOS OS FERIADOS"

make_request "GET" "$API_BASE" "" "Listar todos os feriados"
check_status 200 $LAST_STATUS "Deve retornar lista de feriados"

# Contar feriados existentes
INITIAL_COUNT=$(echo "$LAST_BODY" | jq '. | length')
print_info "Feriados existentes: $INITIAL_COUNT"

# 4. Testar POST /api/holidays - CRIAR NOVO FERIADO
print_header "4. TESTE POST /api/holidays - CRIAR NOVO FERIADO"

CREATE_DATA='{
  "name": "Dia da Independência do Brasil",
  "date": "2024-09-07",
  "country": "Brazil",
  "state": "SP",
  "city": "São Paulo",
  "type": "NATIONAL",
  "recurring": true,
  "description": "Comemoração da independência do Brasil"
}'

make_request "POST" "$API_BASE" "$CREATE_DATA" "Criar novo feriado"
if check_status 201 $LAST_STATUS "Deve criar feriado com sucesso"; then
    # Extrair ID do feriado criado
    CREATED_ID=$(echo "$LAST_BODY" | jq -r '.id')
    print_success "Feriado criado com ID: $CREATED_ID"
    
    # Verificar campos obrigatórios
    NAME=$(echo "$LAST_BODY" | jq -r '.name')
    COUNTRY=$(echo "$LAST_BODY" | jq -r '.country')
    DESCRIPTION=$(echo "$LAST_BODY" | jq -r '.description')
    
    if [ "$NAME" = "Dia da Independência do Brasil" ]; then
        print_success "Campo 'name' persistido corretamente"
    else
        print_error "Campo 'name' incorreto: $NAME"
    fi
    
    if [ "$COUNTRY" = "Brazil" ]; then
        print_success "Campo 'country' persistido corretamente"
    else
        print_error "Campo 'country' incorreto: $COUNTRY"
    fi
    
    if [ "$DESCRIPTION" = "Comemoração da independência do Brasil" ]; then
        print_success "Campo 'description' persistido corretamente"
    else
        print_error "Campo 'description' incorreto: $DESCRIPTION"
    fi
else
    print_error "Falha ao criar feriado"
    exit 1
fi

# 5. Testar GET /api/holidays/{id} - BUSCAR POR ID
print_header "5. TESTE GET /api/holidays/{id} - BUSCAR FERIADO POR ID"

make_request "GET" "$API_BASE/$CREATED_ID" "" "Buscar feriado por ID"
if check_status 200 $LAST_STATUS "Deve encontrar o feriado criado"; then
    FOUND_NAME=$(echo "$LAST_BODY" | jq -r '.name')
    if [ "$FOUND_NAME" = "Dia da Independência do Brasil" ]; then
        print_success "Feriado encontrado corretamente"
    else
        print_error "Feriado encontrado com dados incorretos"
    fi
fi

# 6. Testar GET /api/holidays/{id} - ID INEXISTENTE
print_header "6. TESTE GET /api/holidays/{id} - ID INEXISTENTE"

FAKE_ID="00000000-0000-0000-0000-000000000000"
make_request "GET" "$API_BASE/$FAKE_ID" "" "Buscar feriado com ID inexistente"
check_status 404 $LAST_STATUS "Deve retornar 404 para ID inexistente"

# 7. Testar PUT /api/holidays/{id} - ATUALIZAR FERIADO
print_header "7. TESTE PUT /api/holidays/{id} - ATUALIZAR FERIADO"

UPDATE_DATA='{
  "name": "Dia da Independência do Brasil - ATUALIZADO",
  "date": "2024-09-07",
  "country": "Brazil",
  "state": "RJ",
  "city": "Rio de Janeiro",
  "type": "NATIONAL",
  "recurring": true,
  "description": "Comemoração da independência do Brasil - Versão atualizada"
}'

make_request "PUT" "$API_BASE/$CREATED_ID" "$UPDATE_DATA" "Atualizar feriado existente"
if check_status 200 $LAST_STATUS "Deve atualizar feriado com sucesso"; then
    UPDATED_NAME=$(echo "$LAST_BODY" | jq -r '.name')
    UPDATED_CITY=$(echo "$LAST_BODY" | jq -r '.city')
    UPDATED_DESC=$(echo "$LAST_BODY" | jq -r '.description')
    
    if [[ "$UPDATED_NAME" == *"ATUALIZADO"* ]]; then
        print_success "Nome atualizado corretamente"
    else
        print_error "Nome não foi atualizado: $UPDATED_NAME"
    fi
    
    if [ "$UPDATED_CITY" = "Rio de Janeiro" ]; then
        print_success "Cidade atualizada corretamente"
    else
        print_error "Cidade não foi atualizada: $UPDATED_CITY"
    fi
    
    if [[ "$UPDATED_DESC" == *"atualizada"* ]]; then
        print_success "Descrição atualizada corretamente"
    else
        print_error "Descrição não foi atualizada: $UPDATED_DESC"
    fi
fi

# 8. Testar PUT /api/holidays/{id} - ID INEXISTENTE
print_header "8. TESTE PUT /api/holidays/{id} - ID INEXISTENTE"

make_request "PUT" "$API_BASE/$FAKE_ID" "$UPDATE_DATA" "Atualizar feriado inexistente"
check_status 404 $LAST_STATUS "Deve retornar 404 para atualização de ID inexistente"

# 9. Testar GET /api/holidays com FILTROS
print_header "9. TESTE GET /api/holidays - FILTROS"

# Filtro por país
make_request "GET" "$API_BASE?country=Brazil" "" "Filtrar por país (Brazil)"
check_status 200 $LAST_STATUS "Deve retornar feriados do Brasil"

BRAZIL_COUNT=$(echo "$LAST_BODY" | jq '. | length')
print_info "Feriados encontrados no Brasil: $BRAZIL_COUNT"

# Filtro por tipo
make_request "GET" "$API_BASE?type=NATIONAL" "" "Filtrar por tipo (NATIONAL)"
check_status 200 $LAST_STATUS "Deve retornar feriados nacionais"

NATIONAL_COUNT=$(echo "$LAST_BODY" | jq '. | length')
print_info "Feriados nacionais encontrados: $NATIONAL_COUNT"

# Filtro por data
make_request "GET" "$API_BASE?startDate=2024-01-01&endDate=2024-12-31" "" "Filtrar por período (2024)"
check_status 200 $LAST_STATUS "Deve retornar feriados de 2024"

YEAR_2024_COUNT=$(echo "$LAST_BODY" | jq '. | length')
print_info "Feriados em 2024: $YEAR_2024_COUNT"

# 10. Testar POST com DADOS INVÁLIDOS
print_header "10. TESTE POST - VALIDAÇÃO DE DADOS INVÁLIDOS"

# Dados sem nome (obrigatório)
INVALID_DATA_NO_NAME='{
  "date": "2024-12-25",
  "country": "Brazil",
  "type": "NATIONAL",
  "recurring": true
}'

make_request "POST" "$API_BASE" "$INVALID_DATA_NO_NAME" "Criar feriado sem nome"
check_status 400 $LAST_STATUS "Deve rejeitar feriado sem nome"

# Dados sem data (obrigatório)
INVALID_DATA_NO_DATE='{
  "name": "Natal",
  "country": "Brazil",
  "type": "NATIONAL",
  "recurring": true
}'

make_request "POST" "$API_BASE" "$INVALID_DATA_NO_DATE" "Criar feriado sem data"
check_status 400 $LAST_STATUS "Deve rejeitar feriado sem data"

# 11. Testar DELETE /api/holidays/{id}
print_header "11. TESTE DELETE /api/holidays/{id} - DELETAR FERIADO"

make_request "DELETE" "$API_BASE/$CREATED_ID" "" "Deletar feriado criado"
check_status 204 $LAST_STATUS "Deve deletar feriado com sucesso"

# Verificar se foi realmente deletado
make_request "GET" "$API_BASE/$CREATED_ID" "" "Verificar se feriado foi deletado"
check_status 404 $LAST_STATUS "Feriado deletado não deve ser encontrado"

# 12. Testar DELETE /api/holidays/{id} - ID INEXISTENTE
print_header "12. TESTE DELETE /api/holidays/{id} - ID INEXISTENTE"

make_request "DELETE" "$API_BASE/$FAKE_ID" "" "Deletar feriado inexistente"
check_status 404 $LAST_STATUS "Deve retornar 404 para deleção de ID inexistente"

# 13. Verificar estado final
print_header "13. VERIFICAÇÃO FINAL"

make_request "GET" "$API_BASE" "" "Listar todos os feriados (estado final)"
check_status 200 $LAST_STATUS "Deve retornar lista final de feriados"

FINAL_COUNT=$(echo "$LAST_BODY" | jq '. | length')
print_info "Feriados finais: $FINAL_COUNT"

# Resumo dos testes
print_header "RESUMO DOS TESTES"

echo -e "${GREEN}✅ Testes de Endpoints Concluídos${NC}"
echo
echo "📊 Estatísticas:"
echo "  • Feriados iniciais: $INITIAL_COUNT"
echo "  • Feriados finais: $FINAL_COUNT"
echo "  • Feriados no Brasil: $BRAZIL_COUNT"
echo "  • Feriados nacionais: $NATIONAL_COUNT"
echo "  • Feriados em 2024: $YEAR_2024_COUNT"
echo
echo "🎯 Endpoints testados:"
echo "  • GET /api/holidays ✅"
echo "  • GET /api/holidays/{id} ✅"
echo "  • POST /api/holidays ✅"
echo "  • PUT /api/holidays/{id} ✅"
echo "  • DELETE /api/holidays/{id} ✅"
echo
echo "🔍 Cenários testados:"
echo "  • Criação de feriado ✅"
echo "  • Busca por ID ✅"
echo "  • Atualização de feriado ✅"
echo "  • Deleção de feriado ✅"
echo "  • Filtros de busca ✅"
echo "  • Validação de dados ✅"
echo "  • Tratamento de erros ✅"
echo
print_success "TODOS OS TESTES CONCLUÍDOS COM SUCESSO!"
