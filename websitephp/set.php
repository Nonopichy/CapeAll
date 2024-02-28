<?php
// Use constantes para as credenciais do banco de dados
define('DB_SERVER', 'localhost');
define('DB_USERNAME', 'capeuser');
define('DB_PASSWORD', 'umasenha');
define('DB_NAME', 'cape');

// Conectar ao banco de dados usando constantes
$conn = new mysqli(DB_SERVER, DB_USERNAME, DB_PASSWORD, DB_NAME);

// Verificar a conexão
if ($conn->connect_error) {
    die("Erro na conexão com o banco de dados: " . $conn->connect_error);
}

function tratarString($input) {
    // Remove tags HTML e evita ataques XSS
    $input = htmlspecialchars($input, ENT_QUOTES, 'UTF-8');

    // Remove espaços no início e no final da string
    $input = trim($input);

    // Substitui caracteres especiais por seus equivalentes HTML
    $input = htmlentities($input, ENT_QUOTES, 'UTF-8');

    // Substitui espaços em branco por underscore (_)
    $input = str_replace(' ', '_', $input);

    // Mantém apenas letras, números e underscore (_)
    $input = preg_replace('/[^a-zA-Z0-9_]/', '', $input);

    // Adiciona barras invertidas para escapar caracteres especiais
    $input = addslashes($input);

    return $input;
}

// Verificar se foi enviado um nickname
if (isset($_GET['nickname']) && isset($_GET['cape'])) {
    $nickname = tratarString($_GET['nickname']);
    $cape = tratarString($_GET['cape']);

    // Usar prepared statements para prevenir SQL injection
    $check_query = $conn->prepare("SELECT * FROM jogadores WHERE nickname = ?");
    $check_query->bind_param("s", $nickname);
    $check_query->execute();
    $result = $check_query->get_result();

    if ($result->num_rows > 0) {
        // Atualizar o nickname existente
        $update_query = $conn->prepare("UPDATE jogadores SET cape = ? WHERE nickname = ?");
        $update_query->bind_param("ss", $cape, $nickname);
        if ($update_query->execute()) {
            echo "Cape atualizada para o nickname existente.";
        } else {
            echo "Erro ao atualizar a cape: " . $conn->error;
        }
    } else {
        // Criar um novo nickname
        $insert_query = $conn->prepare("INSERT INTO jogadores (nickname, cape) VALUES (?, ?)");
        $insert_query->bind_param("ss", $nickname, $cape);
        if ($insert_query->execute()) {
            echo "Novo nickname criado com a cape definida.";
        } else {
            echo "Erro ao criar novo nickname: " . $conn->error;
        }
    }
} else {
    echo "Parâmetros necessários não fornecidos.";
}

// Fechar a conexão com o banco de dados
$conn->close();
?>
