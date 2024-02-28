<?php
// Utilizando constantes para credenciais
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

// Utilizando declarações preparadas para prevenir SQL Injection
function tratarString($input) {
    $input = htmlspecialchars($input, ENT_QUOTES, 'UTF-8');
    $input = trim($input);
    $input = htmlentities($input, ENT_QUOTES, 'UTF-8');
    $input = str_replace(' ', '_', $input);
    $input = preg_replace('/[^a-zA-Z0-9_]/', '', $input);
    $input = addslashes($input);
    return $input;
}

// Verificar se foi enviado um array de jogadores via POST no formato JSON
$input = json_decode(file_get_contents('php://input'), true);

if (isset($input['players']) && is_array($input['players'])) {
    $player_list = $input['players'];
    $response = array();

    // Iterar sobre a lista de jogadores
    foreach ($player_list as $player) {
        $nickname = tratarString($player);

        // Utilizando declarações preparadas para prevenir SQL Injection
        $check_query = $conn->prepare("SELECT cape FROM jogadores WHERE nickname = ?");
        $check_query->bind_param("s", $nickname);
        $check_query->execute();
        $result = $check_query->get_result();

        if ($result->num_rows > 0) {
            // Obter a cape associada ao nickname
            $row = $result->fetch_assoc();
            $response[$nickname] = $row['cape'];
        } else {
            // Retornar "default" se o nickname não existir
            $response[$nickname] = "default";
        }
    }

    // Retornar a resposta como JSON
    echo json_encode($response);
} else {
    // Se não houver jogadores enviados via POST, retornar uma mensagem de erro
    echo "Nenhum jogador enviado.";
}

// Fechar a conexão com o banco de dados
$conn->close();
?>