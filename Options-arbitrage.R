library(quantmod)
library(XML)


ticker <-scan(file = "C:\\Users\\www3cam\\Cameron Work\\Quandl\\Quandl.csv", what = character())

OPTlist <- list()
OPTlisttracker = 1
tickername = c("yes")
returnamount = c(-10)
expiration_date = c("yes")
strike_price = c("yes")
strategy = c("yes")

maxlist = data.frame(tickername, returnamount,expiration_date,strike_price,strategy,stringsAsFactors=FALSE)


for ( i in 1:length(ticker)) {
  ticker1 = ticker[i]
  
  inputticker = substring(ticker1,6,nchar(ticker1))
  t = tryCatch(
    getSymbols(inputticker, src = "google", auto.assign = FALSE, from = '2015-5-20'), 
    error = function(e) e
      )
  if(!inherits(t, "error")){
    
    currentprice = t[NROW(t),4][[1]][1]
    Optionchain = tryCatch(
      {
        getOptionChain(inputticker, Exp = NULL)
      }, error = function(e) e
    )
    
    if(!inherits(Optionchain, "error")){
      
      
      
      if (length(Optionchain) > 0)
      {
        for (j in 1:length(Optionchain))
        {
          calls = Optionchain[[j]][1]
          puts = Optionchain[[j]][2]
          string = rownames(calls[[1]])[1]
          currentdate = Sys.Date()
          date = substring(string,nchar(inputticker) + 1,nchar(inputticker) + 6)
          dateexpire = as.Date(ISOdate(paste("20",substring(date,1,2), sep = ""),substring(date,3,4),substring(date,5,6)))
          daysbeforeexp = as.numeric(difftime(dateexpire,currentdate,units = "days"),units="days")
          daysbeforeexp = daysbeforeexp + .25
          for (k in 1:NROW(calls[[1]]))
          {
            strike = calls[[1]][k,1]
            putrow = -1
            for (j in 1:NROW(puts[[1]]))
            {
              if (strike == puts[[1]][j,1])
              {
                putrow = j
              }
            }
            
            if (putrow == -1)
            {
              break
            }
            
            volumec = calls[[1]][k,6]
            Outstandingc = calls[[1]][k,7]
            volumep = puts[[1]][putrow,6]
            outstandingp = puts[[1]][putrow,7]
            
            
            if ((volumec > 1 || Outstandingc > 10) && (volumep > 1 || outstandingp > 10) && !(is.na(volumec))&& !(is.na(Outstandingc))&& !(is.na(volumep))&& !(is.na(outstandingp)))
            {
              
              
              if (is.na(strike))
              {
                break
              }
              
              if (is.na(currentprice))
              {
                break
              }
              
             
              dif = strike - currentprice 
              if (dif/currentprice > .5)
              {
                break
              }
              lastc = as.numeric(calls[[1]][k,2])
              lastp = as.numeric(puts[[1]][putrow,2])
              if (is.na(lastc))
              {
                lastc = 0
              }
              
              if (is.na(lastp))
              {
                lastp = 0
              }
              
              #go long the future
              longc = as.numeric(calls[[1]][k,5])
              shortp = as.numeric(puts[[1]][putrow,4])
              shortc = as.numeric(calls[[1]][k,4])
              longp = as.numeric(puts[[1]][putrow,5])
              
              if (is.na(longc))
              {
                longc = 0
              }
              
              if (is.na(shortc))
              {
                shortc = 0
              }
              if (is.na(longp))
              {
                longp = 0
              }
              if (is.na(shortp))
              {
                shortp = 0
              }
              
              if (lastc > longc)
              {
                longc = lastc
              } else if (lastc < shortc)
              {
                shortc = lastc
              }
              
              if (lastp < shortp)
              {
                shortp = lastp
              } else if (lastp > longp)
              {
                longp = lastp
              }
              
              
              
              shortp = shortp*100-.5
              longc = longc*100+.5
              longp = longp *100 + .5
              shortc = shortc*100 - .5
              arbreturn = 0
              
              if(shortc < 0)
              {
                shortc = 0
              }
              
              if(shortp < 0)
              {
                shortp = 0
              }
              
              if (shortp-longc > dif*100+1)
              {
                #long future short stock
                arbreturn = (365/daysbeforeexp)*(shortp-longc-(dif*100 + 1))/(shortp+longc+currentprice*100+1)
                
                if (arbreturn > .05)#change back to 25%
                {
              
                  
                  returnlist = c(toString(inputticker),arbreturn,toString(dateexpire),toString(strike),"longf shorts")
                  maxlist = rbind(maxlist,returnlist)
                  
                  
                }
                
                
                
              } else if (longp-shortc < dif*100-1) 
              {
                
                #short future long stock
                arbreturn = (365/daysbeforeexp)*((dif*100 - 1)-(longp-shortc))/(longp+shortc+currentprice*100-1)
                
                if (arbreturn > .05)#change back to 22%
                {

                  
                  returnlist = c(toString(inputticker),arbreturn,toString(dateexpire),toString(strike),"shortf longs")
                  maxlist = rbind(maxlist,returnlist)
                  
                  
                  
                }
                
                
                
              }
              
              
              
            }
            
            
            
            
            
          }
          
          
        }
        
        
        
        
      }
      
      
      
      
    }
    
    
    
    
  }
        
        
}

maxlist = maxlist[order(-rank(maxlist[,2])),]
      
      
    
    
  
  
  
  
  