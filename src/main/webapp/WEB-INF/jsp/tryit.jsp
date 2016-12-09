<!doctype html>
<html>
<head>
    <style type="text/css">
        .grid {
            margin: 1em auto;
            border-collapse: collapse
        }
        
        .grid td {
            cursor: pointer;
            width: 30px;
            height: 30px;
            border: 1px solid #ccc;
            text-align: center;
            font-family: sans-serif;
            font-size: 13px width: 15px;
            height: 15px;
            text-align: center;
            cursor: pointer;
            background-size: contain;
            background-position: center;
            background-image: url(gems/V.png)
        }
        
        .grid td.selected {
            opacity: 0.5;
            background-color: yellow;
            font-weight: bold;
            color: red;
        }
        
        .grid td.cursed {
            background-color: black;
            font-weight: bold;
            color: red;
        }
        
        .grid img {
            display: block;
            max-width: 30px;
            max-height: 30px;
            width: auto;
            height: auto;
        }
    </style>
</head>
<body>
    <input type="text" value=" (x1 | !x2 | x3) &amp;&amp; (!x1 | x2 | x4) &amp;&amp; ( x1 | !x3 | !x4)" size="35" id="formula">
    <input type="button" value="Generate Instance!" id="generate">
    <div style="text-align:center; font-size: 16pt;">Score: <span id='score'></span></div>
    <script language="javascript">
        var lastClicked = [];
        var selectedArr = [];
        var field;
        var corruptedCell = 0;

        function createArray(length) {
            var arr = new Array(length || 0),
                i = length;
            if (arguments.length > 1) {
                var args = Array.prototype.slice.call(arguments, 1);
                while (i--) arr[length - 1 - i] = createArray.apply(this, args);
            }
            return arr;
        }


        function updateCell(x, y, gem, type) {
            this.x = x //riga
            this.y = y //colonna
            this.gem = gem //tipo gemma (R A B C D RC(rossa verticale) RR(rossa orizzontale))
            this.type = type //tipo cella (CC cursed cell , HC hole cell)
        }


        function Cell(x, y, gem, type) {
            this.x = x //riga
            this.y = y //colonna
            this.gem = gem //tipo gemma (R A B C D RC(rossa verticale) RR(rossa orizzontale))
            this.type = type //tipo cella (CC cursed cell , HC hole cell
            this.updateCell = updateCell;
        }

        //TODO  bug prima variabile not non prende "(!XX" ma solo "( !XX"
        function parse_formula(formula) {
            formula = formula.replace(/[\s\(\)]+/g, '')
            formula = formula.replace(/[âˆ¨\|]+/g, '|')
            formula = formula.replace(/[âˆ§\&]+/g, '&')
            formula = formula.replace(/[-\!]+/g, '!')
            formula = formula.replace(/[Â¬\!]+/g, '!')
            clauses = formula.split('&');
            var parsed_formula = []
            var var_mapping_pos = []
            var numvars = 0
            for (var i = 0; i < clauses.length; i++) {
                var vars = clauses[i].split('|')
                var parsed_clause = []
                for (var j = 0; j < vars.length; j++) {
                    if ((!/^[!]?[A-Za-z]\w*$/.test(vars[j])) || (vars.length != 3))
                        return null
                    var v = vars[j]
                    var buff = v
                    if (buff[0] == '!') {
                        buff = buff.split('!')
                        buff.shift()
                        buff.toString()
                    }
                    if (!(buff in var_mapping_pos)) {
                        numvars++
                        var_mapping_pos[buff] = numvars
                    }
                    if (v[0] == '!') {
                        if (parsed_clause.indexOf(var_mapping_pos[buff]) == -1)
                            parsed_clause.push(-var_mapping_pos[buff])
                    } else if (parsed_clause.indexOf(var_mapping_pos[buff]) == -1)
                        parsed_clause.push(var_mapping_pos[buff])
                }

                if (parsed_clause.length == 0)
                    continue
                if (parsed_clause.length > 3)
                    return null
                parsed_formula.push(parsed_clause)
            }
            if (parsed_formula.length > 0)
                return {
                    formula: parsed_formula,
                    numvars: numvars
                }
            return null
        }
        //build a matrix using the reduction function starting from the 3-sat formula
        function buildField(numvars, formula, mouseOverF) {
            var numclauses = formula.length
            var cols = 6 * numvars + 3 + 1
            var rows = 4 * numclauses + 3 + 2
            var buff = createArray(rows, cols)
            var k = 0;
            var grid = document.createElement('table');
            grid.className = 'grid';
            for (var i = 0; i < rows; i++) {
                for (var j = 0; j < cols; j++) {
                    // VOID
                    if (i % 2 == 0) {
                        if (j % 2 == 0) {
                            buff[i][j] = new Cell(i, j, 'A', 'C')
                        } //1
                        else {
                            buff[i][j] = new Cell(i, j, 'D', 'C')
                        } //2
                    } else {
                        if (j % 2 == 0) {
                            buff[i][j] = new Cell(i, j, 'B', 'C')
                        } //3
                        else {
                            buff[i][j] = new Cell(i, j, 'C', 'C')
                        } //4
                    }
                }
            }
            for (var i = 0; i < rows; i++) {
                for (var j = 1; j < cols; j++) {

                    if ((i > rows - 4) && (j < cols - 3)) { // CHOICE

                        if (((j - 1) % 6 == 1) || ((j - 1) % 6 == 3)) { //seconda quarta colonna 
                            if (i == rows - 3) {
                                //jelly speciale rossa verticale V
                                buff[i][j] = new Cell(i, j, 'RC', 'C')

                            } else if (i % 2 == 0) {
                                buff[i][j] = new Cell(i, j, 'A', 'C')
                            } //1 void
                            else {
                                buff[i][j] = new Cell(i, j, 'H', 'C')
                            } //2 void
                        }
                        if ((j - 1) % 6 == 2) { //terza colonna 
                            if (i % 2 == 0) {
                                buff[i][j] = new Cell(i, j, 'D', 'C')
                            } //1
                            else {
                                buff[i][j] = new Cell(i, j, 'A', 'C')
                            } //2
                            if ((i > rows - 3)) {
                                //pallina rossa
                                buff[i][j] = new Cell(i, j, 'R', 'C')
                                if (i == rows - 1) {
                                    buff[i][j] = new Cell(i, j, 'R', 'CC')
                                }
                            }
                        }
                        if (((j - 1) % 6 == 0) || ((j - 1) % 6 == 4)) { //terza colonna 
                            if (i == (rows - 2)) buff[i][j] = new Cell(i, j, 'H', 'C')
                        }
                    }
                    if ((i < (numclauses * 4)) && (j > cols - 4)) { //CLAUSE
                        if (j % 2 == 0) {
                            if (i % 4 == 0) {
                                buff[i][j] = new Cell(i, j, 'R', 'CC')

                            } //1
                            else {
                                if (i % 4 == 1) {
                                    buff[i][j] = new Cell(i, j, 'H', 'C')
                                }
                            } //2
                        } else {
                            if (i % 4 == 0) {
                                buff[i][j] = new Cell(i, j, 'D', 'C')

                            } //1
                            else {
                                if (i % 4 == 1) {
                                    buff[i][j] = new Cell(i, j, 'H', 'C')
                                }
                            } //2
                        }
                    }

                }
            }
            for (var i = 0; i < formula.length; i++) {
                for (var j = 0; j < formula[i].length; j++) {
                    var temp = formula[i][j];
                    var buffX = i * 4;
                    if (temp > 0) {
                        temp--
                        buff[buffX][temp * 6 + 1 + 1] = new Cell(buffX, temp * 6 + 2 - 1, 'RR', 'C');
                        buff[buffX + 1][temp * 6 + 2 - 1] = new Cell(buffX + 1, temp * 6 + 2 - 2, 'H', 'C');
                        buff[buffX + 1][temp * 6 + 2] = new Cell(buffX + 1, temp * 6 + 2 - 1, 'H', 'C');
                        buff[buffX + 1][temp * 6 + 2 + 1] = new Cell(buffX + 1, temp * 6 + 2 - 0, 'H', 'C');
                    } else {
                        temp++
                        buff[buffX][(-temp) * 6 + 3 + 1] = new Cell(buffX, (-temp) * 6 + 2 + 1, 'RR', 'C');
                        buff[buffX + 1][(-temp) * 6 + 4 - 1] = new Cell(buffX + 1, (-temp) * 6 + 2 + 2, 'H', 'C');
                        buff[buffX + 1][(-temp) * 6 + 4] = new Cell(buffX + 1, (-temp) * 6 + 2 + 1, 'H', 'C');
                        buff[buffX + 1][(-temp) * 6 + 4 + 1] = new Cell(buffX + 1, (-temp) * 6 + 2 + 0, 'H', 'C');
                    }
                }
            }
            return buff;
        }
        //Draw the entire table and adds listener and images for each cell of the table
        function drawField(dfield) {
            var numclauses = formula.length
            var k = 0;
            var grid = document.createElement('table');
            for (var i = 0; i < dfield.length; i++) {
                var tr = grid.appendChild(document.createElement('tr'))
                for (var j = 0; j < dfield[1].length; j++) {
                    var cell = tr.appendChild(document.createElement('td'));
                    cell.setAttribute("id", i + "-" + j);
                    cell.innerHTML = ''
                    cell.style.backgroundRepeat = "no-repeat"
                    var img = document.createElement('img');
                    img.setAttribute("id", 'img_' + i + '_' + j);
                    if (dfield[i][j].gem == 'RR' || dfield[i][j].gem == 'RC') {
                        if (dfield[i][j].type == 'CC') {
                            img.src = 'gems/' + dfield[i][j].gem + 'CC.png';
                        } else {
                            img.src = 'gems/' + dfield[i][j].gem + '.png';
                        }
                    } else {
                        if (dfield[i][j].type == 'CC') {
                            img.src = 'gems/' + dfield[i][j].gem + 'CC.png';
                        } else {
                            img.src = 'gems/' + dfield[i][j].gem + '.png';
                        }
                    }
                    img.addEventListener('mouseover', (function(el, i, j, k) {
                        return function() {
                            mouseOver(el, i, j, k);
                        }
                    })(cell, i, j, k), false);
                    img.addEventListener('click', (function(el, i, j, k) {
                        return function() {
                            mouseClick(el, i, j, k);
                        }
                    })(cell, i, j, k), false);
                    cell.appendChild(img);
                }
            }
            return grid;
        }

        //Draw the entire table and adds the images for each cell of the table without listener bgecoming unresponsive(during animations)
        function drawSimpleField(dfield) {
            var numclauses = formula.length
            var k = 0;
            var grid = document.createElement('table');
            for (var i = 0; i < dfield.length; i++) {
                var tr = grid.appendChild(document.createElement('tr'))
                for (var j = 0; j < dfield[1].length; j++) {
                    var cell = tr.appendChild(document.createElement('td'));
                    cell.setAttribute("id", i + "-" + j);
                    cell.innerHTML = ''
                    cell.style.backgroundRepeat = "no-repeat"
                    var img = document.createElement('img');
                    img.setAttribute("id", 'img_' + i + '_' + j);
                    if (dfield[i][j].gem == 'RR' || dfield[i][j].gem == 'RC') {
                        if (dfield[i][j].type == 'CC') {
                            img.src = 'gems/' + dfield[i][j].gem + 'CC.png';
                        } else {
                            img.src = 'gems/' + dfield[i][j].gem + '.png';
                        }
                    } else {
                        if (dfield[i][j].type == 'CC') {
                            img.src = 'gems/' + dfield[i][j].gem + 'CC.png';
                        } else {
                            img.src = 'gems/' + dfield[i][j].gem + '.png';
                        }
                    }
                    cell.appendChild(img);
                }
            }
            return grid;
        }
        //Clean the table resetting it to the actual value of the field structure
        function clean() {
            for (var i = 0; i < field.length; i++) {
                for (var j = 0; j < field[i].length; j++) {
                    document.getElementById(i + '-' + j).className = '';
                    draw(i, j);
                }
            }
            explode = [];
        }

        //reset a cell in the table giving is position(if field was updated this will update the table on screen)
        function draw(i, j) {
            document.getElementById('score').textContent = score
            if (field[i][j].gem == 'RR' || field[i][j].gem == 'RC') {
                if (field[i][j].type == 'CC') {
                    document.getElementById('img_' + i + '_' + j).src = 'gems/' + field[i][j].gem + 'CC.png';
                } else {
                    document.getElementById('img_' + i + '_' + j).src = 'gems/' + field[i][j].gem + '.png';
                }
            } else {
                if (field[i][j].type == 'CC') {
                    document.getElementById('img_' + i + '_' + j).src = 'gems/' + field[i][j].gem + 'CC.png';
                } else {
                    document.getElementById('img_' + i + '_' + j).src = 'gems/' + field[i][j].gem + '.png';
                }
            }
        }

        var clickCount = true;
        var explode = [];
        //this listener implements the behavour when the mouse is passed on the images of the table(jelly) 
        function mouseOver(el, row, col, i) {
            var coordEl = el.id.split("-")
            var xE = coordEl[0];
            var yE = coordEl[1];
            //keep empty the arrey list untile the first click
            if (clickCount) {
                while (lastClicked.length > 0)
                    for (var i = 0; i < lastClicked.length; i++) {
                        var cell = lastClicked.pop();
                        var coord = cell.id.split("-")
                        var x = coord[0];
                        var y = coord[1];
                        if (field[x][y].type == 'CC')
                            cell.className = 'cursed';
                        else cell.className = '';
                    }
                lastClicked = [];
                explode = [];
            }
            //after the first click if there are already elements in the list will be checked for bindings and then added
            if ((!clickCount) && (lastClicked.length > 0)) {
                var cnt = lastClicked[lastClicked.length - 1]
                var coord = cnt.id.split("-")
                var x = coord[0];
                var y = coord[1];
                if (lastClicked.indexOf(el) != -1) {//if already in the stack the top elements will popped until they are different
                    while ((!(el == cellt) && lastClicked.length > 1)) {
                        var cellt = lastClicked.pop();
                        var coordt = cellt.id.split("-")
                        var xt = coordt[0];
                        var yt = coordt[1];
                        if (field[xt][yt].type == 'CC')
                            cellt.className = 'cursed';
                        else cellt.className = '';
                    }
                } else if ((field[xE][yE].gem != 'H') && 
                    (field[xE][yE].gem != 'K') &&
                    (field[x][y].gem[0] == field[row][col].gem[0])) {
                    if ((Math.abs(x - row) == 1 && (y == col)) || 
                        (Math.abs(y - col) == 1 && (x == row)) ||
                      (Math.abs(y - col) == 1 && Math.abs(x - row) == 1)) {
                        //if respect binings of the original game will be added
                        lastClicked.push(el);
                    }
                }
            }//select the new jelly that will be exploded with the jelly selected
            selectAll(selectExplosion(lastClicked).reverse());
        }
        //clean the table on DOM and the select the new ones
        function selectAll(gems) {
            clean();//clean table
            for (var i = 0; i < gems.length; i++) {//select all passed gems
                var el = gems[i];
                explode.push(el);
                el.className = 'selected';
            }
            return gems;
        }
        //computes the first explosion and the gems hitted(cause its behaviour is different than the next one)
        function selectExplosion(selectedPop) {
            var gemSelected = [];
            var gemSelected2 = [];
            var gemSelected3 = [];
            var RR = 0;
            var RC = 0;
            var x = -1;
            var y = -1;
            for (var i = 0; i < selectedPop.length; i++) {//copy gems and count the specials one
                var cnt = selectedPop[i];
                gemSelected.push(cnt)
                gemSelected3.push(cnt)
                var coord = cnt.id.split("-")
                x = coord[0];
                y = coord[1];
                if (field[x][y].gem == 'RR') {
                    RR = RR + 1;
                }
                if (field[x][y].gem == 'RC') {
                    RC = RC + 1;
                }
            }//check for special condition of special jelly like the real game
            if (RR + RC > 1) {
                gemSelected2 = gemSelected2.concat(selectRow(x, y));
                gemSelected2 = gemSelected2.concat(selectCol(x, y));
            } else if (RR > 0) {
                gemSelected2 = gemSelected2.concat(selectRow(x, y));
            } else if (RC > 0) {
                gemSelected2 = gemSelected2.concat(selectCol(x, y));
            }
            while (gemSelected3.length > 0) {//remove double element
                var tmp = gemSelected2.indexOf(gemSelected3.pop());
                if (tmp >= 0) {
                    gemSelected2.splice(tmp, 1);
                }
            }//select the chain reaction of other special jelly if will be hitted.
            gemSelected2 = selectMoreExplosions(gemSelected2);
            while (gemSelected.length > 0) {
                gemSelected2.push(gemSelected.pop());
            }
            return gemSelected2;//returns the whole selection
        }


        //select other gems recursively starting after the first selection
        function selectMoreExplosions(gemToSelect) {
            var gemSelected = [];
            while (gemToSelect.length > 0) {
                var el = gemToSelect.pop();
                var xy = el.id.split("-");
                var x = xy[0];
                var y = xy[1];
                if (gemSelected.indexOf(el) == -1) {
                    gemSelected.push(el)
                    if (field[x][y].gem == 'RR') {
                        gemToSelect = gemToSelect.concat(selectRow(x, y));
                    } else {
                        if (field[x][y].gem == 'RC') {

                            gemToSelect = gemToSelect.concat(selectCol(x, y));
                        }
                    }
                }
            }
            return gemSelected;
        }
        //remove the explosions image with a grass image
        function clearExplosions() {
            for (var i = 0; i < field.length; i++) {
                for (var j = 0; j < field[1].length; j++) {
                    if (field[i][j].gem == 'K')
                        field[i][j].gem = 'V'
                }
            }
        }
        //find and remove the previus grid if exist and than create the next one
        function appendGrid(field) {
            elem = document.getElementById('grid');
            if (elem != null) {
                elem.parentNode.removeChild(elem)
            }
            var grid = drawField(field)
            grid.className = 'grid';
            grid.setAttribute("id", "grid");
            document.body.appendChild(grid);
        }
        //same as appendGrid but without listener
        function appendSimpleGrid(field) {
            elem = document.getElementById('grid');
            if (elem != null) {
                elem.parentNode.removeChild(elem)
            }
            var grid = drawSimpleField(field)
            grid.className = 'grid';
            grid.setAttribute("id", "grid");
            document.body.appendChild(grid);
        }

        var score = 0;
        //first call, and functions for generate button. It reset everything based on the current formula of text-box
        function generate() {
            score = 0;
            document.getElementById('score').textContent = score
            var f = parse_formula(document.getElementById('formula').value)
            while (lastClicked.length > 0)
                for (var i = 0; i < lastClicked.length; i++)
                    var cell = lastClicked.pop();
            if (!f) {
                alert('There was a problem while parsing your formula')
                return
            }
            formula = f.formula;
            numvars = f.numvars;
            corruptedCell = formula.length + numvars;
            field = buildField(numvars, formula, mouseOver)
            var order = createArray(field[0].length);
            for (var i = order.length - 1; i >= 0; i--) {
                order[i] = 1
            }
            appendGrid(field);
        }
        //check if the victory requirment are fully satisfated
        function endlessVictory(field) {
            if (corruptedCell == 0)
                alert('You won the game! Click generate to have a new game :D')
        }
        //implement the behaviour when the user click on images of table
        function mouseClick(el, row, col, i) {
            var coordEl = el.id.split("-")
            var xE = coordEl[0];
            var yE = coordEl[1];
            if ((lastClicked.length >= 3) && (!clickCount)) {//if is the second click and there are more than 3 jelly in sthe stack will cause the explosion
                appendSimpleGrid(field)
                lineCounter = 0;
                stepSelect(explode, 0, delayNormal);
            }
            if (!clickCount) {//if are less than 3 jelly the stack will be cleaned out and the table too
                clean();
                for (var i = 0; i < lastClicked.length; i++) {
                    var cell = lastClicked[i];
                    var coord = cell.id.split("-")
                    var x = coord[0];
                    var y = coord[1];
                    if (field[x][y].type == 'CC')
                        cell.className = 'cursed';
                    else cell.className = '';
                }
                lastClicked = [];
            }//if this is the first click and the stack is empty and is a jelly, the latter will be pushed in the stack
            if ((clickCount) && (lastClicked.length == 0) && (field[xE][yE].gem != 'K') && (field[xE][yE].gem != 'H')) {
                lastClicked.push(el);
                el.className = 'selected';
            }
            if (clickCount) clickCount = false
            else clickCount = true
        }

        var delaySpecial = 25;
        var delayNormal = 15;
        var delayDrop = 1;
        //implement a timeout chain that will show the executions of the explosions
        function stepSelect(gemToPop, i, delay) {
            popSingleGem(gemToPop, i);//explode one gem
            i++;
            if (i < gemToPop.length) {//if there are more explosions to do
                setTimeout(
                    (function(gemToPop, i, delay) {
                        return function() {
                            stepSelect(gemToPop, i, delay)//call the next explosion with a delay
                        }
                    }(gemToPop, i, delay)),
                    delay);
            } else {
                if (lineCounter == 0) {
                    setTimeout(
                        (function() {
                            stepDrop(true);//if there are no more explosions will start the drop of jelly
                        }), 1)
                } else {
                    //lineCounter--
                }
            }
        }
        //given a row it select all the grid cell of the DOM starting by a position alternatly
        function selectRow(row, k) {
            var seq = [];
            var flag = true
            var i = 0;
            var j = parseInt(k);
            while (flag) {
                i++
                if ((j - i) >= 0) {
                    var cell = document.getElementById(row + '-' + (j - i))
                    seq.push(cell)
                }
                if ((j + i) < field[0].length) {
                    var cell = document.getElementById(row + '-' + (j + i))
                    seq.push(cell)
                }
                if (((j + i) > field[0].length) && ((j - i) < 0)) {
                    flag = false;
                }
            }
            return seq;
        }

         //given a column it select all the grid cell of the DOM starting by a position alternatly
        function selectCol(k, col) {
            var seq = [];
            var flag = true
            var j = 0;
            var i = parseInt(k)
            while (flag) {
                j++
                if ((i - j) >= 0) {
                    var cell = document.getElementById((i - j) + '-' + col)
                    seq.push(cell)
                }
                var boolea = ((i + j) <= field.length);
                if ((i + j) < field.length) {
                    var cell = document.getElementById((i + j) + '-' + col)
                    seq.push(cell)
                }
                if (((i + j) > field.length) && ((i - j) < 0)) {
                    flag = false;
                }
            }
            return seq;
        }
        //make a single jelly to explode, check if the cell was corrupted and update the score, and than update the table too
        function popSingleGem(gemToPop, i) {
            var coord = gemToPop[i].id;
            var xy = coord.split("-");
            var x = xy[0];
            var y = xy[1];
            if (field[x][y].type == 'CC') {
                score = score + 1000;
                corruptedCell--;
                field[x][y].type = 'C';
            }
            if ((field[x][y].gem != 'H') && (field[x][y].gem != 'K') && (field[x][y].gem != 'V')) {
                if (field[x][y].gem == 'RR') {
                    score = score + 100;
                    field[x][y].gem = 'K'
                } else {
                    if (field[x][y].gem == 'RC') {
                        score = score + 100;
                        field[x][y].gem = 'K'
                    } else {
                        field[x][y].gem = 'K'
                        score = score + i;
                    }
                }
            }
            draw(x, y)
        }
        //make a row of gems to fall or end the animations resetting the grd with the one with listeners
        function stepDrop(flag) {
            if (flag) {
                sandCell(field.length - 1, false);
                appendSimpleGrid(field)
            } else {
                clearExplosions();
                appendGrid(field);
                endlessVictory(field);
            }
            return false;
        }
        //make a line to fall down for each line starting from down, until there are jelly that moves
        function sandCell(i, flag) {
            for (var j = field[i].length - 1; j >= 0; j--) {
                var tmp = dropSingleGem(i, j);
                flag = (tmp || flag)
            }
            if ((i == 0)) {
                setTimeout((function(flag) {
                        return function() {
                            stepDrop(flag)
                        }
                    }(flag)),
                    delayDrop);
            } else {
                setTimeout((function(flag, i) {
                        return function() {
                            sandCell(i - 1, flag)
                        }
                    }(flag, i)),
                    25);
            }
        }
        //move down a jelly or create a new one, return true if the jelly was moved otherwise false
        function dropSingleGem(i, j) {
            var flag = false;
            if ((field[i][j].gem != 'K')) {
                return flag;
            } else {
                if (i == 0) {
                    var rand = Math.random();
                    if (rand < (0.25)) {
                        field[i][j].gem = 'A'
                    } else {
                        if (rand < (0.50)) {
                            field[i][j].gem = 'B'
                        } else {
                            if (rand < (0.75)) {
                                field[i][j].gem = 'C'
                            } else {
                                field[i][j].gem = 'D'
                            }
                        }
                    }
                    flag = true;
                } else { //up
                    if ((i - 1 >= 0) && (j >= 0) && (field[i - 1][j].gem != 'K') && (field[i - 1][j].gem != 'H') && (field[i - 1][j].gem != 'V')) {
                        field[i][j].gem = (field[i - 1][j].gem)
                        field[i - 1][j].gem = 'K'
                        flag = true;
                    } else {
                        //up-right
                        if ((i - 1 >= 0) && (j + 1 < field[0].length) &&
                            (field[i - 1][j + 1].gem != 'K') &&
                            (field[i - 1][j + 1].gem != 'H') &&
                            (field[i - 1][j + 1].gem != 'V') &&
                            (field[i][j + 1].gem != 'K') &&
                            (field[i][j + 1].gem != 'V')
                        ) {
                            field[i][j].gem = (field[i - 1][j + 1].gem)
                            field[i - 1][j + 1].gem = 'K';
                            flag = true
                        } else { // up-left
                            if ((i - 1 >= 0) && (j - 1 >= 0) &&
                                (field[i - 1][j - 1].gem != 'K') &&
                                (field[i - 1][j - 1].gem != 'H') &&
                                (field[i - 1][j - 1].gem != 'V') &&
                                (field[i][j - 1].gem != 'K') &&
                                (field[i][j - 1].gem != 'V')) {
                                field[i][j].gem = (field[i - 1][j - 1].gem)
                                field[i - 1][j - 1].gem = 'K'
                                flag = true;
                            }
                        }
                    }
                }
            }
            return flag;
        }
        document.getElementById('generate').onclick = generate
        generate()
    </script>
</body>

</html>