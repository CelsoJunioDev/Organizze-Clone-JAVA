# Organizze (CLONE) - Java
#### Aplicativo para controle de finanças, desenvolvido para a plataforma Android.

* Autenticação feita através do Firebase Auth.
* Após o usuário efetuar o cadastro, é possível adicionar receitas e despesas com os campos: Valor, Data, Categoria e descrição.
* Possível deslizar para o lado para deletar uma transação, e o valor é automaticamente acrescido/subtraido do saldo total.
* Todos os dados são atualizados em tempo real ultilizando o Realtime Database, do Firebase.
* Se o usuário estiver sem internet no momento da solicitação, as alterações serão salvas localmente e, assim que a conexão for restabelecida, serão salvas no DB.
* Foi ultilizada uma biblioteca para melhorar o CalendarView. 
