function ansstruct = commoditycorrel(vector)
%last data calculated should be from two quarters ago.  Data from last
%quarter is used to predict. make sure numentries is one less than total
%entries
    Quandl.auth('EgkfAsfbxwaWyyKZaBCP');

    [~,tickers,~] = xlsread('Quandl.csv');%added a two
    iterations = length(tickers);
    disp(iterations);
    numentries1 = length(vector) - 1;
    limit = sqrt(.5);
    arraynum = 1;
    
    for i = 1:iterations
        disp(tickers{i})
        try
            matrix = Quandl.get(tickers{i},'type','data','start_date','2003-06-01'); %date can be changed if data is longer
        catch
            try
            matrix = Quandl.get(tickers{i},'type','data');
            catch
                break
            end
        end
        datavec = [matrix(:,1), matrix(:,11)];
        correlvec = datecalc(datavec,numentries1);
        size1 = numentries1; 
        while correlvec(size1) == 0
            size1  = size1 - 1;
        end
        if size1 > 8
            commoditycor = vector(2:size1+1,:);
            [~,c] = size(commoditycor);
            maxr = 0;
            for k = 1:c
                rtemp = corr2(correlvec(1:size1),commoditycor(:,k));
                maxr = max(maxr,abs(rtemp));
            end

            if maxr >= limit
               ones1 = ones(size1,1);
               commoditycor= [ones1, commoditycor];
               [b,confinv,~,~,stats] = regress(correlvec(1:size1),commoditycor);
               predvalue = b(1); 
               for j = 2:length(b)
                   predvalue = predvalue + b(j)*vector(1,j-1);
               end
               predvalue = correlvec(1,1)*exp(predvalue);
               updownlim = datavec(1,2)*(exp(.2)-1);
               if abs(predvalue-datavec(1,2))>updownlim
                    ansstruct(arraynum).name = tickers{i};
                    ansstruct(arraynum).mathstats = stats;
                    ansstruct(arraynum).confinterval = confinv;
                    ansstruct(arraynum).size = size1;
                    ansstruct(arraynum).predictedvalue = predvalue;
                    ansstruct(arraynum).currentprice = datavec(1,2);
                    ansvec = [predvalue; correlvec];
                    ansvec = [ansvec, vector];
                    ansstruct(arraynum).array = ansvec;
                    arraynum = arraynum + 1;
               end
            end
        end
    end
end







