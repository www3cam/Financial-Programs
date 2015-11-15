%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%	STOCHASTIC LIFE-CYCLE MODEL SOLVED WITH DYNAMIC PROGRAMMING
%
%    max \sum_{t=0}^T u(c_t)
%
%    subject to
%
%    w_{t+1} = (1+r)*w_t + y_t - c_t   for t <= R
%    w_{t+1} = (1+r)*w_t - c_t   for R < t <= T
%
%    y_t follows a two-state Markov chain
%
%  George Hall
%  Brandeis University
%  October 2010
%
%   Beta-Delta discounting
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
function [] = lifemodel5(consumbase)
  ! rm lifecycle.out
  diary lifecycle.out;
  disp('STOCHASTIC ASSET ALLOCATED LIFE-CYCLE MODEL SOLVED WITH DP');
  disp('');
  %
  %  set parameter values
  %
  flag1 = false;
  delta = .95;
  sigma  = 2;             % degree of relative risk aversion
  beta   = 0.95;            % subjective discount factor
  y  = 50000;               % labor income which is constant
  r = 0;                    % return of investment
  Stockmin = .1;              %Min % of stocks 
  Stockmax =1;               %Max % of stocks
  stockinc = .1;              % size of the stock grid increment
  s = (Stockmin:stockinc:Stockmax)';          % in stocks
  ns = round((Stockmax-Stockmin)/stockinc+1); % provices the # of stock increments

  stdevstock = .195;        % stock stdev
  stdevbond = .04;         % t bill stdev

  avestock = .047;         % stock ave return
  avebills = .005;        % tbill ave return

  wmmax = 1600000;
  wmax = 1300000; % maximum value of wealth grid
  wmin = 0;                 % minimum value of wealth grid
  nw   = 131; % number of grid points
  wgrid = zeros(nw,1);
  
  wspace = 1.3;    
  wgrid(1)=wmin;
  for i=2:(nw);
    wgrid(i) = wgrid(i-1) + (wmax-wgrid(i-1))/(nw-i+1)^wspace;
  end
wgrid(end)=wmmax;
  %
  %  tabulate the utility function such that for zero or negative
  %  consumption utility remains a large negative number so that
  %  such values will never be chosen as utility maximizing
  %

  wp = repmat(wgrid,1,nw);
  w  = repmat(wgrid,1,nw)';

  consummatrix = zeros(nw);
  for i = 1:nw;
    for j = 1:nw;
      consummatrix(i,j) = consumbase;
    end
  end



  cons = y + w - wp;
  cons_retire = w - wp;
  
  util = zeros(nw);
  util_retire = zeros(nw);
  
  for i = 1:nw
     for j=1:nw
          if (cons(i,j)-consummatrix(i,j))>0
              util(i,j) = reallog(cons(i,j)-consummatrix(i,j));
              %util(i,j) = ((-(cons(i,j)-consummatrix(i,j)).^(1-sigma))./(1-sigma));
          elseif (cons(i,j) - consummatrix (i,j)) == 0
              util(i,j) = 0;
          elseif (cons(i,j) -consummatrix (i,j)) < 0
              util(i,j) = -2*reallog((consummatrix(i,j)-cons(i,j)));
              %util(i,j) = 2*(((consummatrix(i,j)-cons(i,j)).^(1-sigma))./(1-sigma));
          end 
          if (cons_retire(i,j)-consummatrix(i,j))>0
              util_retire(i,j) = reallog(cons_retire(i,j)-consummatrix(i,j));
              %util(i,j) = ((-(cons_retire(i,j)-consummatrix(i,j)).^(1-sigma))./(1-sigma));
          elseif (cons_retire(i,j) - consummatrix (i,j)) == 0
              util_retire(i,j) = 0;
          elseif (cons_retire(i,j) -consummatrix (i,j)) < 0
              util_retire(i,j) = -2*reallog((consummatrix(i,j)-cons_retire(i,j)));
              %util(i,j) = 2*(((consummatrix(i,j)-cons_retire(i,j)).^(1-sigma))./(1-sigma));
          end 
          if (cons(i,j) < 0)
              util(i,j) = -inf;
          end
          if(cons_retire(i,j) < 0)
              util_retire(i,j) = -inf;
          end
     end
  end

  
  %util(cons<=0) = -inf;
  %util_retire(cons_retire<=0) = -inf;
  

  clear cons cons_retire

  %
  %  initialize some variables
  %
  %
  T = 65;   % number of periods in the agents life
  R = 45;   % retirement age
  v = zeros(nw,T+1);
  stockallocation = zeros(nw,T);
  tdecis   = zeros(nw,T);
  returngrid = zeros(nw,T+1); %grid for optimal allocation given an amount saved
  clear w wp;
  %
  %  penalize the agent if he/she dies with negative wealth
  %
  v(wgrid< 0,:,T+1) = -inf;
  %
  %  solve the model backwards: since a lifecycle model is finite horizon
  %  model, just solve the Bellman equation backwards T periods.
  %
  format short g

  for t=T:-1:R+1;
    %
    %This is a expected value calculator for the utility gained for a maximum allocation of stock
    %
    %
    for h = 1:1:nw;
      nsgrid = zeros(ns,1);
      for i = 1:1:ns 
            perstock = Stockmin+(i-1)*stockinc; %amount in stock
            value = integral2(@(r1,r2) (interp1(wgrid,v(:,t+1),((wgrid(h).*(perstock*(1+r1) + (1-perstock).*(1+r2)))),'linear','extrap').*normpdf(r1,avestock,stdevstock).*normpdf(r2,avebills,stdevbond)),avestock - 5*stdevstock,avestock + 5 * stdevstock, avebills - 5*stdevbond, avebills+5*stdevbond);
            nsgrid(i,1) = value;
      end
      [returngrid(h,t),stockallocation(h,t)] = max(nsgrid);
    end
   
    [tv2, tdecis2] = max(util_retire + beta*delta*repmat(returngrid(:,t),1,nw));
    [tv1,tdecis1]=max(util_retire + beta*repmat(returngrid(:,t),1,nw));
    tdecis(:,t)=tdecis2';
    v(:,t)=tv1';

     fprintf('just solved period %3.0f \n',t);

  end

  for t=R:-1:1;
    %
    %This is a expected value calculator for the utility gained for a maximum allocation of stock
    %
    %
    for h = 1:1:nw;
      nsgrid = zeros(ns,1);
      for i = 1:1:ns 
            perstock = Stockmin+(i-1)*stockinc; %amount in stock
            value = integral2(@(r1,r2) (interp1(wgrid,v(:,t+1),((wgrid(h).*((perstock*(1+r1) + (1-perstock).*(1+r2))))),'linear','extrap').*normpdf(r1,avestock,stdevstock).*normpdf(r2,avebills,stdevbond)),avestock - 5*stdevstock,avestock + 5 * stdevstock, avebills - 5*stdevbond, avebills+5*stdevbond);
            %fprintf('value %3.0f,', value);
            nsgrid(i,1) = value;
      end
      [returngrid(h,t),stockallocation(h,t)] = max(nsgrid);
    end
    
    [tv2, tdecis2] = max(util + beta*delta*repmat(returngrid(:,t),1,nw));
    [tv1,tdecis1]=max(util + beta*repmat(returngrid(:,t),1,nw));
    tdecis(:,t)=tdecis2';
    v(:,t)=tv1';
    %disp(tdecis)tdecis is correct!
    fprintf('just solved period %3.0f \n',t);

  end
  
  decis = zero(nw,T);
  assignin('base', 'tdecis5', tdecis);
  for  i = 1:nw
      for j = 1:T
          decis(i,j) = wgrid(tdecis(i,j));
      end
  end
  assignin('base', 'decis5', decis);
   
  stockdecis=(stockallocation - 1).*stockinc+Stockmin;
  assignin('base','stockdecis5', stockdecis);
  %
  %    simulate a life history of the agent
  %
  disp('SIMULATING LIFE HISTORY');
  wmark = find(wgrid==0);
  wealth = wgrid(wmark,1);        % initial level of assets
  states   = zeros(T,2);
  controls = zeros(T,2);
  
  %
  %  simulate the life of an agent
  %  Note: I didn't automatically include interest rates so I will have to do
  %  a search for the correct location for the chaining
  
  for i = 1:T;
      saving_decis = tdecis(wmark,i);
      stockallot = stockdecis(saving_decis,i);
      r = (stockallot*(normrnd(avestock,stdevstock))+(1-stockallot)*(normrnd(avebills,stdevbond)));
      
      money = (wgrid(saving_decis))*(1+r);
      %fprintf('i = %3.0f, money = %3.0f, interest rate = %3.0f /t', i, money, r)
      if i<=R;
        cons = y + wealth-(wgrid(saving_decis));
        %disp(cons)
        %pause
        income = y;
      else
        cons = wealth - (wgrid(saving_decis));
        income = 0;
      end
      stockcontrol = stockallot;
      wealth = money;
      i = 1;
      while wgrid(i) < money && money < wmmax
         i = i + 1; 
      end
      if (money - wgrid(i-1)) < (wgrid(i)-money)
          wmark = i-1;
      else
          wmark = i;
      end
      if wmark > nw
          wmark = nw;
          flag1 = true;
      end
      if wmark < 1
          wmark = 1;
      end

      states(i,:) = [ wealth  income ];
      controls(i,:) = [ cons stockcontrol ];
  end
  %
  %   plot some info:
  %
  str = sprintf('LIFE-CYCLE MODEL: SIMULATED INCOME & CONSUMPTION WITH EXPECTED CONSUMPTION: %f',consumbase);
  if flag1 == true
      fprintf('Warning wealth ceilling not high enough')
  end
  figure(1)
  plot((1:T)',controls(:,1),'-',(1:T)',states(:,2),':','LineWidth',1.5);
  title(str);
  axis([ 1 (T) 0 1.025*max(y,max(controls(:,1))) ])
  print consum.ps

  str2 = sprintf('LIFE-CYCLE MODEL: SIMULATED CONSUMPTION & STOCK ALLOCATION WITH EXPECTED CONSUMPTION: %f',consumbase);

  figure(2)
  plot((1:T)',controls(:,2),'LineWidth',1.5);
  title(str2,'LineWidth',1.5);
  print wealth.ps

  diary off;
end









