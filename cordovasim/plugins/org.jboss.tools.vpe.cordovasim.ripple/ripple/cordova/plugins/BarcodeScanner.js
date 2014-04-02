var emulatorBridge = ripple('emulatorBridge');
module.exports = {
    scan: function (success, error, args) {
      var scanDialog = $('<div id="scanDialog" class="scanDialog">'),
          fieldSet = $('<fieldset id="skanDialogFieldSet" class="skanDialogFieldSet">'),

          formatDropDownLabel = $('<label for="formatDropDown">Format:</label>'),
          formatDropDown = $('<select id="formatDropDown">'),
          qrCodeFormat = $('<option value="QR_CODE">QR_CODE</option>'),
          dataMatrixFormat = $('<option value="DATA_MATRIX">DATA_MATRIX</option>'),
          upcEFormat = $('<option value="UPC_E">UPC_E</option>'),
          upcAFormat = $('<option value="UPC_A">UPC_A</option>'),
          ean8Format = $('<option value="EAN_8">EAN_8</option>'),
          ean13Format = $('<option value="EAN_13">EAN_13</option>'),
          code128Format = $('<option value="CODE_128">CODE_128</option>'),
          code39Format = $('<option value="CODE_39">CODE_39</option>'),

          scannedTextLabel = $('<label for="scannedText">Scanned Text:</label>'),
          scannedText = $('<input type="text" id=scannedText>'),

          okButton = $('<button id="barcodeSkan-ok" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only small-button"><span class="ui-button-text">Ok</span></button>'),
          cancelButton = $('<button id="barcodeScan-cancel" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only small-button"><span class="ui-button-text">Cancel</span></button>');
      
      try {
            scannedTextLabel.appendTo(fieldSet);
            scannedText.appendTo(fieldSet);
               
            // inizializing format Drop Down
            qrCodeFormat.appendTo(formatDropDown);
            dataMatrixFormat.appendTo(formatDropDown);
            upcEFormat.appendTo(formatDropDown);
            upcAFormat.appendTo(formatDropDown);
            ean8Format.appendTo(formatDropDown);
            ean13Format.appendTo(formatDropDown);
            code128Format.appendTo(formatDropDown);
            code39Format.appendTo(formatDropDown);
            
            formatDropDownLabel.appendTo(fieldSet);    
            formatDropDown.appendTo(fieldSet);

            fieldSet.appendTo(scanDialog);

            okButton.appendTo(scanDialog);
            cancelButton.appendTo(scanDialog);

            scanDialog.appendTo(window.document.body);
            
            document.getElementById('barcodeSkan-ok').addEventListener("click", function() {
                var result = new Object();
                result.cancelled = false; 
                result.text = $('#scannedText').val();
                result.format = $('#formatDropDown option:selected').text();
                $("#scanDialog").dialog('close');
                success(result);
            });

           document.getElementById('barcodeScan-cancel').addEventListener("click", function() {
                var result = new Object();
                result.cancelled = true; 
                result.text = "";
                result.format = ""; 
                $("#scanDialog").dialog('close');
                success(result);
            });

            $("#scanDialog").dialog({
                  autoOpen: true,
                  modal: true,
                  title: "Barcode Scanner",
                  heigh: 50,
                  width: 510,
                  position: 'center',
                  resizable: false
              }).bind('dialogclose', function() {
                 $(this).dialog('destroy').remove(); // Destroying dialog on close
              });

        } catch(err) {
          error("Fail to emulate BarcodeScanner.scan() method");
        }
    },

    encode: function (success, error, args) {
       var data = args[0].data,
           type = args[0].type,
           url = "https://chart.googleapis.com/chart?cht=qr&chs=300x300&chld=L|5&chl=" + data, // Using google QR service - https://developers.google.com/chart/infographics/docs/qr_codes
           backgroundMask = $('<div id="background-mask">'),
           barcodeWrapper = $('<div id="barcode-wrapper">'),
           barcodeImage = $('<img id="barcode-image">'),
           barcodeData = $('<div id="barcode-data">' + data + '</div>');  
           barcodeBackButtonFunction = function() {
              var barcodeWrapper = emulatorBridge.window().document.getElementById('barcode-wrapper');
              var backgroundMask = emulatorBridge.window().document.getElementById('background-mask');
              
              barcodeWrapper.parentNode.removeChild(barcodeWrapper);
              backgroundMask.parentNode.removeChild(backgroundMask);
              
              emulatorBridge.window().document.removeEventListener("backbutton", barcodeBackButtonFunction, false); // Force one time execution of the listener
           };

        barcodeImage.attr("src", url);
        barcodeImage.attr("style",  "position: relative;" 
                                  + "z-index: 2147483647;"// max value of z-index
                                  + "width: 100%;");
        barcodeImage.appendTo(barcodeWrapper);

        barcodeData.attr("style", "position: relative;"                                
                                + "text-align: center;"
                                + "font-family: Tahoma, Geneva, sans-serif;" 
                                + "font-size: 25px;"
                                + "color: black;" 
                                + "bottom: 30px;"
                                + "padding: 10px;"
                                + "z-index: 2147483647;" 
                                + "background-color: white;");
        barcodeData.appendTo(barcodeWrapper);

        barcodeWrapper.attr("style", "position: absolute;"
                                  +  "top: 0;" 
                                  +  "right: 0;" 
                                  +  "bottom: 0;"  
                                  +  "left: 0;" 
                                  +  "width: 100%;"  
                                  +  "height: 100%;"  
                                  +  "min-height: 100%;"
                                  +  "z-index: 2147483647;" 
                                  +  "background-color: white;");
        
        backgroundMask.attr("style", "position: fixed;"  // div covers 100% of page (not screen) - need it for devices with a really small screens
                                   + "left: 0px;" 
                                   + "top: 0px;" 
                                   + "width: 100%;" 
                                   + "height: 100%;" 
                                   + "z-index: 2147483646;" 
                                   + "background-color: white;");

        barcodeWrapper.appendTo(emulatorBridge.window().document.body);
        backgroundMask.appendTo(emulatorBridge.window().document.body);
        emulatorBridge.window().document.addEventListener("backbutton",  barcodeBackButtonFunction, false);    
//      success(); // ?! under Android VM success function is never called
    }
};