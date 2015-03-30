            function moveOptionsAcross(fromSelectList, toSelectList) {
                  var selectOptions = fromSelectList.getElementsByTagName('option');  
                  for (var i = 0; i < selectOptions.length; i++) {
                           var opt = selectOptions[i];
                           if (opt.selected) {
                                 fromSelectList.removeChild(opt);
                                 toSelectList.appendChild(opt);
                                  // originally, this loop decremented from length to 0 so that you 
                                  // wouldn't have to worry about adjusting the index. However, then 
                                  // moving multiple options resulted in the order being reversed from when 
                                  // was in the original selection list which can be confusing to the user. 
                                  // So now, the index is adjusted to make sure we don't skip an option.      
                                  i--; 
                           }
                 }
            }
  
            function moveOptionsUp(selectId) { 
                var selectList = document.getElementById(selectId);
                var selectOptions = selectList.getElementsByTagName('option'); 
                for (var i = 1; i < selectOptions.length; i++) {  
                    var opt = selectOptions[i];  
                    if (opt.selected) {   
                        selectList.removeChild(opt);
                        selectList.insertBefore(opt, selectOptions[i - 1]); 
                    }    
                }
            }


            function moveOptionsDown(selectId) { 
                var selectList = document.getElementById(selectId);
                var selectOptions = selectList.getElementsByTagName('option');
                for (var i = selectOptions.length - 2; i >= 0; i--) { 
                    var opt = selectOptions[i]; 
                    if (opt.selected) {  
                        var nextOpt = selectOptions[i + 1];
                        opt = selectList.removeChild(opt);
                        nextOpt = selectList.replaceChild(opt, nextOpt);
                        selectList.insertBefore(nextOpt, opt);     
                    }    
                }
            }


            function submitForm(){
                var selectOptions = $('selected').getElementsByTagName('option');
                for(var i=0;i<selectOptions.length;i++){
                    selectOptions[i].selected=true;
                }
                return true;
            }
