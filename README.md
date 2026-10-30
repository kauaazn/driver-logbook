# Rota — Diário de Bordo

Aplicação para registrar turnos, inspeções e avarias de uma frota. A interface web em Next.js consome uma API REST em Java/Spring Boot; o PostgreSQL guarda o histórico.

## Funcionalidades

- Cadastro de veículos com número de frota, modelo e placa.
- Início e encerramento de turno com hodômetro e inspeções de saída e retorno.
- Validação de um único turno aberto e de quilometragem final crescente na API.
- Registro de avarias vinculadas ao veículo, com estados aberta, em manutenção e resolvida.
- Histórico com filtro por veículo e data inicial, resumo de horas e exportação CSV.
- Interface responsiva e mensagens de estado para carregamento, API indisponível e listas vazias.

## Executar localmente

Requer Node.js 20.19+, Java 21+, Maven 3.9+ e Docker com Compose.

1. Inicie o PostgreSQL:

   ```bash
   docker compose up -d database
   ```

2. Em um terminal, inicie a API:

   ```bash
   cd backend
   mvn spring-boot:run
   ```

   A API fica em `http://localhost:8080`. O endpoint `GET /api/health` informa se ela está disponível.

3. Em outro terminal, inicie a interface:

   ```bash
   cd frontend
   npm install
   npm run dev
   ```

   Abra `http://localhost:3000`. A interface espera a API em `http://localhost:8080`. Para usar outro endereço, defina `NEXT_PUBLIC_API_URL` antes de iniciar o Next.js.

Ao abrir pela primeira vez, cadastre um veículo para iniciar o fluxo. O banco é criado pelo Compose; o Hibernate atualiza o esquema durante a execução.

## Como estudar a arquitetura

1. **Interface — `frontend/app/page.tsx`:** carrega o dashboard, histórico e frota. `refresh` consulta `GET /api/dashboard`; `act` envia as ações de formulário e atualiza a tela quando o servidor confirma.
2. **Componentes de interação:** `Dialog` concentra os formulários de veículo, turno, avaria e encerramento. A interface oferece feedback, mas as regras de negócio também são conferidas pelo servidor.
3. **Entrada da API — `DiarioApplication.java`:** configura Spring Boot e libera CORS para o endereço local do Next.js.
4. **Modelo — `Domain.java`:** define veículo, turno, inspeção e avaria. Relações JPA ligam inspeções e turnos aos veículos e preservam as datas de criação e alteração.
5. **Acesso a dados — `Repositories.java`:** Spring Data gera consultas para localizar turnos abertos e devolver o histórico em ordem decrescente.
6. **Regras e HTTP — `ApiController.java`:** recebe DTOs validados, verifica invariantes do fluxo e retorna códigos HTTP úteis (201 na criação, 409 para conflito e 422 para hodômetro inválido).
7. **Banco — `docker-compose.yml`:** inicia PostgreSQL com volume persistente. As credenciais locais são apenas para desenvolvimento; configure segredos próprios antes de publicar uma instância.

## Endpoints principais

| Método | Caminho | Uso |
| --- | --- | --- |
| `GET` | `/api/dashboard` | Dados do resumo, turnos, veículos e avarias |
| `GET`, `POST` | `/api/vehicles` | Consultar e cadastrar veículos |
| `GET`, `POST` | `/api/shifts` | Histórico e início de turno |
| `POST` | `/api/shifts/{id}/finish` | Encerrar turno e registrar inspeção final |
| `GET`, `POST` | `/api/damages` | Consultar e registrar avarias |
| `PATCH` | `/api/damages/{id}/status?status=EM_MANUTENCAO` | Atualizar o estado de uma avaria |

## Escopo e próximos passos

O projeto é uma demonstração local sem autenticação: não use dados reais de motoristas. Para uma implantação multiusuário, os próximos passos são autenticação, vínculo entre cada registro e seu proprietário, migrações versionadas com Flyway, paginação do histórico e trilha de auditoria mais detalhada. Os filtros e tabelas atuais foram pensados para o fluxo de demonstração e podem ser expandidos conforme o uso.

## Tecnologias

Next.js · React · TypeScript · Java 21 · Spring Boot · Spring Data JPA · Bean Validation · PostgreSQL · Docker Compose
