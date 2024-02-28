<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Nickname Confirmation</title>
</head>
<body>
    <form action="set.php" method="get">
        <label for="nickname">Nickname:</label>
        <input type="text" id="nickname" name="nickname" maxlength="16" required>
        <label for="cape">Cape:</label>
        <select id="cape" name="cape" required>
            <option value="default">Nenhuma</option>
            <option value="2011">Minecon 2011</option>
            <option value="2012">Minecon 2012</option>
            <option value="2013">Minecon 2013</option>
            <option value="2015">Minecon 2015</option>
            <option value="2016">Minecon 2016</option>
            <option value="bday">Birthday</option>
            <option value="cherry">Cherry Blossom</option>
            <option value="classic">Classic</option>
            <option value="cobalt">Cobalt</option>
            <option value="db">dB (Debuger)</option>
            <option value="migrator">Migrator</option>
            <option value="customer">Millionth Customer</option>
            <option value="mojang">Mojang</option>
            <option value="mojira">Mojira</option>
            <option value="studios">Mojang Studios</option>
            <option value="prisma">Prismarine</option>
            <option value="realms">Realms Mapmaker</option>
            <option value="scrolls">Scrolls</option>
            <option value="snowman">Snowman</option>
            <option value="spade">Spade</option>
            <option value="trans">Translator</option>
            <option value="chitrans">Translator (Chinese)</option>
            <option value="japtrans">Translator (Japanese)</option>
            <option value="turtle">Turtle</option>
            <option value="valentin">Valentine</option>
            <option value="vanilla">Vanilla</option>
            <option value="ostand">Optifine Default</option>
            <option value="oalblack">Optifine All Black</option>
            <option value="oblack">Optifine Black</option>
            <option value="ocyan">Optifine Cyan</option>
            <option value="ogreen">Optifine Green</option>
            <option value="ogray">Optifine Gray</option>
            <option value="opurple">Optifine Purple</option>
            <option value="ored">Optifine Red</option>
            <option value="owhite">Optifine White</option>
            <option value="oyellow">Optifine Yellow</option>
        </select>
        <button type="submit">Confirmar</button>
    </form>
</body>
</html>