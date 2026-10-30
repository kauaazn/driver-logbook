# Guia de estudo — Diário Motorista

O jeito mais fácil de entender o projeto é seguir um registro do clique na tela até a linha persistida no banco.

## 1. Mapeie as partes

- `frontend/app/page.tsx`: tela, estados, filtros, formulários e chamadas HTTP.
- `backend/src/main/java/com/portfolio/diariodebordo/ApiController.java`: caminhos REST, DTOs e regras de validação.
- `backend/src/main/java/com/portfolio/diariodebordo/Domain.java`: entidades e relações JPA.
- `backend/src/main/java/com/portfolio/diariodebordo/Repositories.java`: consultas geradas pelo Spring Data.
- `docker-compose.yml`: serviço PostgreSQL para desenvolvimento local.

## 2. Siga o cadastro de veículo

1. O botão abre `Dialog` com `type="vehicle"`.
2. O formulário guarda os campos e chama `onSubmit('/vehicles', ...)`.
3. Em `Home`, `act` transforma isso em um `POST` para a API.
4. `createVehicle` recebe um `VehicleRequest`, rejeita campos vazios e verifica se o número de frota já existe.
5. `VehicleRepository.save` persiste a entidade; o servidor responde com HTTP 201.
6. `refresh` consulta o dashboard e React atualiza a tela.

Percorra essa sequência lendo `Dialog`, `act` e `createVehicle`. Desenhe setas entre as três partes no papel.

## 3. Compare validação visual e validação do servidor

O formulário usa atributos como `required` e `min` para orientar a pessoa. A regra definitiva fica no backend: `@Valid` valida os DTOs e o código de `finishShift` impede quilometragem final menor que a inicial.

Isso importa porque um cliente HTTP pode enviar uma requisição sem passar pela interface. Regra de negócio precisa continuar valendo nesse caso.

## 4. Entenda as entidades e relações

`Vehicle` é referenciado por `Shift` e `Damage`. `Inspection` pertence a um `Shift`, com uma fase `INICIAL` ou `FINAL` e um item inspecionado. Avarias têm um `DamageStatus` e datas de criação e atualização.

Nos endpoints de início e encerramento, `saveInspection` guarda os itens enviados junto com a etapa correspondente.

## 5. Siga o fluxo de um turno

1. `startShift` procura um turno sem `endedAt`; se houver, retorna conflito HTTP 409.
2. Localiza o veículo, cria o turno com `startedAt` e `startKm` e salva a inspeção inicial.
3. `finishShift` verifica se o turno existe e ainda está aberto.
4. Compara `endKm` com `startKm`; se for menor, devolve HTTP 422.
5. Salva quilometragem final, observação, horário de encerramento e inspeção final.

## 6. Faça estes exercícios

1. Abra DevTools → Network e inicie um turno. Anote o método, caminho, JSON enviado e resposta.
2. Tente iniciar um segundo turno sem encerrar o primeiro. Localize a condição no backend e o status HTTP.
3. Envie uma quilometragem final inferior à inicial e identifique onde a API bloqueia a operação.
4. Crie uma avaria e mude seu estado. Trace a chamada `PATCH` até `updateDamage`.
5. Adicione um campo ao DTO de veículo. Acompanhe os efeitos no formulário, controller e entidade.

## 7. Limites que você deve saber explicar

Este primeiro recorte não implementa login nem separação de dados por motorista. Também não tem migrações versionadas; o Hibernate atualiza o esquema para facilitar a demonstração local. Explique isso como escopo da versão atual e descreva autenticação e Flyway como próximos passos, sem afirmar que já existem.

## 8. Pratique uma explicação curta

Tente responder sem ler o código: como um turno começa e termina? Quais regras só o backend pode garantir? Como uma inspeção fica ligada a um turno e a um veículo?
