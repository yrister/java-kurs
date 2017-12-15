<%@ page import="com.madnes.StaticResource" %>
<%@ page import="com.madnes.ConfigManager" %><%--
  Created by IntelliJ IDEA.
  User: Roman
  Date: 06/09/2017
  Time: 09:43
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% ConfigManager.setGameParamFromConfig(); %>
<html>
<head>
    <title>mad game</title>
    <link rel="stylesheet" type="text/css" href="style.css">
</head>
<style>
    #mainCanvas {
        border-radius: <%= StaticResource.agentSize / 2 %>px;
        margin-top: 15px;
    }
</style>
<body>
<center>
    <canvas id="mainCanvas" width="<%= StaticResource.fieldWidth %>px"
            height="<%= StaticResource.fieldHeight %>px"></canvas>
</center>
<div class="gameinfo">
    <table>
        <tr>
            <td></td>
            <td><img src="berries.png"></td>
            <td><img src="ladybug.png"></td>
            <td><img src="bee.png"></td>
            <td><img src="firefly.png"></td>
        </tr>
        <tr class="border_bottom">
            <td>Количество</td>
            <td id="numberArti">0</td>
            <td id="numberGroup1">0</td>
            <td id="numberGroup2">0</td>
            <td id="numberGroup3">0</td>
        </tr>
        <tr class="border_bottom">
            <td>Сумма энергии</td>
            <td id="energyArti">0</td>
            <td id="energyGroup1">0</td>
            <td id="energyGroup2">0</td>
            <td id="energyGroup3">0</td>
        </tr>
        <tr>
            <td>Суммарное поражение</td>
            <td></td>
            <td id="damageGroup1">0</td>
            <td id="damageGroup2">0</td>
            <td id="damageGroup3">0</td>
        </tr>
    </table>
    <div class="control">
        <button onclick="start()">start</button>
        <button onclick="stop()">stop</button>
    </div>
</div>
<div id="footer">
    statistic
</div>
<script>
    var isPlaying = false;

    function start() {
        var xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function () {
            isPlaying = true
            updaterCount++;
            updateAgentState(updaterCount);
        }
        xhr.open('POST', 'start', true);
        xhr.send(null);
    }

    function stop() {
        var isDidPlaying = isPlaying;
        var xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function () {
            if (!isDidPlaying && !isPlaying) {
                agents = []
                artifacts = []
                updaterCount++;
                drawField();
            }
        }
        xhr.open('POST', 'stop', true);
        xhr.send(null);
    }

    var agents = [];
    var artifacts = [];

    // agent size
    var size = <%= StaticResource.agentSize %>;

    // field size
    var mainWidth = <%= StaticResource.fieldWidth %>;
    var mainHeigth = <%= StaticResource.fieldHeight %>;

    ///////////////
    // images

    var img = new Image;
    img.src = "grass.png"
    var intro = new Image;
    intro.src = "intro.png"
    var outtro = new Image;
    outtro.src = "outtro.png"

    var group1Image = new Image;
    group1Image.src = "ladybug.png"
    var group2Image = new Image;
    group2Image.src = "bee.png"
    var group3Image = new Image;
    group3Image.src = "firefly.png"

    var artifactImage1 = new Image;
    artifactImage1.src = "raspberry.png"
    var artifactImage2 = new Image;
    artifactImage2.src = "cherries.png"
    var artifactImage3 = new Image;
    artifactImage3.src = "grapes.png"
    var artifactImage4 = new Image;
    artifactImage4.src = "berries.png"

    //////////////////////////

    var mainConvas = document.getElementById("mainCanvas");
    var hdc = mainConvas.getContext('2d');

    window.onload = function () {
        hdc.drawImage(img, 0, 0, mainWidth, mainHeigth);
        updateAgentState(updaterCount)
    }

    hdc.strokeStyle = 'red';
    hdc.lineWidth = 2;

    var lastTime = 0;
    var lastTimes = [];
    var updaterCount = 0;

    function showIntro() {
        hdc.drawImage(intro, 0, 0, mainWidth, mainHeigth);
    }

    function showOuttro(group) {
        hdc.drawImage(outtro, 0, 0, mainWidth, mainHeigth);
        var asize = mainHeigth * 0.3;
        hdc.drawImage(agentImage(group), mainWidth / 2 - asize / 2, mainHeigth / 2 - asize * 0.75, asize, asize);
    }

    function agentImage(group) {
        if (group == 0) {
            return group1Image
        } else if (group == 1) {
            return group2Image
        } else {
            return group3Image
        }
    }

    function artifactImage(group) {
        if (group == 0) {
            return artifactImage1
        } else if (group == 1) {
            return artifactImage2
        } else if (group == 2) {
            return artifactImage3
        } else {
            return artifactImage4
        }
    }

    function drawRotatedImage(ctx, image, x, y, angle) {
        ctx.save();
        ctx.translate(x + size / 2, y + size / 2);
        ctx.rotate(angle);
        ctx.drawImage(image, -(size / 2), -(size / 2), size, size);
        ctx.restore();
    }

    function drawField() {
        hdc.drawImage(img, 0, 0, mainWidth, mainHeigth);
        var energy = [0, 0, 0];
        var number = [0, 0, 0];
        var damage = [0, 0, 0];
        var aliveCount = 0;
        var lastAliveGroup;
        for (var i = 0; i < agents.length; i++) {
            var group = agents[i]["groupID"] - 1;
            damage[group] += agents[i]["damage"];
            if (!agents[i]["isVisable"]) {
                continue;
            }
            var x = agents[i]["x"];
            var y = agents[i]["y"];
            var image = agentImage(group);
            var angleInRadians = agents[i]["direction"];

            drawRotatedImage(hdc, image, x, y, angleInRadians);

            energy[group] += agents[i]["energy"];
            number[group] += 1;
            aliveCount += 1;
            lastAliveGroup = group;
        }
        var artiEnergy = 0;
        for (var i = 0; i < artifacts.length; i++) {
            hdc.drawImage(artifactImage(artifacts[i]["type"]), artifacts[i]["x"], artifacts[i]["y"], artifacts[i]["size"], artifacts[i]["size"]);
            artiEnergy += artifacts[i]["energy"]
        }

        if (aliveCount == 1 && !isPlaying) {
            showOuttro(lastAliveGroup);
        } else if (!isPlaying && agents.length == 0) {
            showIntro();
        }

        document.getElementById("energyGroup1").innerHTML = energy[0] | 0;
        document.getElementById("energyGroup2").innerHTML = energy[1] | 0;
        document.getElementById("energyGroup3").innerHTML = energy[2] | 0;

        document.getElementById("numberGroup1").innerHTML = number[0];
        document.getElementById("numberGroup2").innerHTML = number[1];
        document.getElementById("numberGroup3").innerHTML = number[2];

        document.getElementById("damageGroup1").innerHTML = damage[0] | 0;
        document.getElementById("damageGroup2").innerHTML = damage[1] | 0;
        document.getElementById("damageGroup3").innerHTML = damage[2] | 0;

        document.getElementById("energyArti").innerHTML = artiEnergy | 0;
        document.getElementById("numberArti").innerHTML = artifacts.length;
    }

    function printFPS() {
        if (lastTime != 0) {
            lastTimes.push(Date.now() - lastTime)
        }
        lastTime = Date.now();
        if (lastTimes.length > 30) {
            lastTimes.shift();
        }
        var fps = 0;
        lastTimes.forEach(function (item, i, arr) {
            fps += item;
        });
        document.getElementById("footer").innerHTML = (fps / lastTimes.length) | 0;
    }

    function updateAgentState(counter) {
        printFPS();
        if (updaterCount != counter) {
            return
        }

        var xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function () {
            if (xhr.readyState == 4) {
                var data = xhr.responseText;
                agents = [];
                artifacts = [];
                JSON.parse(data, function (k, v) {
                    if (k == "agents") {
                        agents = v;
                    } else if (k == "artifacts") {
                        artifacts = v;
                    } else if (k == "isPlaying") {
                        isPlaying = v
                    }
                    return v;
                });
                drawField();
                if (isPlaying) {
                    setTimeout(function() {
                        updateAgentState(counter)
                    }, 35);
                } else {
                    lastTime = 0;
                    lastTimes = [];
                    document.getElementById("footer").innerHTML = 0;
                }
            }
        };
        xhr.open('GET', 'game', true);
        xhr.send(null);
    }
</script>
</body>
</html>
