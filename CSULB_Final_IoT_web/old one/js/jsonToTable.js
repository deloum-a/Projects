
function jsonToTableVerSpe(objArray, theme, value, header) {

    needHeader = true;
    var jsonArray = [];

    for (var i = objArray.length - 1; i >= 0; i--) {
        jsonArray.push(typeof objArray[i] != 'object' ? JSON.parse(objArray[i]) : objArray[i]);
    };
    
    
    var str = '<table class="' + theme + '">';
    var array;
    // Only create table head if needHeader is set to True..
    if (needHeader) {
        str += '<thead><tr>';
        for (var i = jsonArray.length - 1; i >= 0; i--) {
            array = jsonArray[i];
            for (var index in array[0]) {
                if (index == value)
                    str += '<th scope="col">' + header[i] + '</th>';
            }
        }
        str += '</tr></thead>';
    }

    // table body
    str += '<tbody>';
    var tmp = jsonArray[0];
    for (var i = 0; i < tmp.length; i++) {
        str += (i % 2 == 0) ? '<tr class="alt">' : '<tr>';
        for (var j = jsonArray.length - 1; j >= 0; j--) {
            array = jsonArray[j];
            if (value in array[i])
                str += '<td>' + array[i][value] + '</td>';
        }
        str += '</tr>';
    }
    str += '</tbody>'
    str += '</table>';
    return str;
}

/*
function jsonToTableHor(objArray, theme, value, header) {

    needHeader = true;
        
    var array = typeof objArray != 'object' ? JSON.parse(objArray) : objArray;
    
    var str = '<table class="' + theme + '">';

    // table body
    str += '<tbody>';
    
        str += (i % 2 == 0) ? '<tr class="alt">' : '<tr>';

        str += '<thead><tr>';
        for (var index in array[0]) {
            if (index == value)
                str += '<th scope="col">' + header + '</th>';
        }

    for (var i = 0; i < array.length; i++) {    
        for (var index in array[i]) {
            if (index == value)
                str += '<td>' + array[i][index] + '</td>';
        }
    }
    str += '</tr></thead>';
    str += '</tbody>'
    str += '</table>';
    return str;
}
function jsonToTableVer(objArray, theme, value, header) {

    needHeader = true;
        
    var array = typeof objArray != 'object' ? JSON.parse(objArray) : objArray;
    
    var str = '<table class="' + theme + '">';

    // Only create table head if needHeader is set to True..
    if (needHeader) {
        str += '<thead><tr>';
        for (var index in array[0]) {
            if (index == value)
                str += '<th scope="col">' + header + '</th>';
        }
        str += '</tr></thead>';
    }

    // table body
    str += '<tbody>';
    for (var i = 0; i < array.length; i++) {
        str += (i % 2 == 0) ? '<tr class="alt">' : '<tr>';
        for (var index in array[i]) {
            if (index == value)
                str += '<td>' + array[i][index] + '</td>';
        }
        str += '</tr>';
    }
    str += '</tbody>'
    str += '</table>';
    return str;
}*/