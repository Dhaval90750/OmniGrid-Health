import React from "react";

export const Card: React.FC<React.HTMLAttributes<HTMLDivElement>> = ({ 
  children, 
  className = "", 
  ...props 
}) => {
  return (
    <div 
      className={`bg-background border border-border rounded-[8px] shadow-[0_1px_3px_rgba(0,0,0,0.08)] ${className}`}
      {...props}
    >
      {children}
    </div>
  );
};

export const CardHeader: React.FC<React.HTMLAttributes<HTMLDivElement>> = ({ children, className = "", ...props }) => (
  <div className={`px-6 py-4 border-b border-border ${className}`} {...props}>
    {children}
  </div>
);

export const CardTitle: React.FC<React.HTMLAttributes<HTMLHeadingElement>> = ({ children, className = "", ...props }) => (
  <h3 className={`text-base font-medium text-text-primary ${className}`} {...props}>
    {children}
  </h3>
);

export const CardContent: React.FC<React.HTMLAttributes<HTMLDivElement>> = ({ children, className = "", ...props }) => (
  <div className={`p-6 ${className}`} {...props}>
    {children}
  </div>
);

export const CardFooter: React.FC<React.HTMLAttributes<HTMLDivElement>> = ({ children, className = "", ...props }) => (
  <div className={`px-6 py-4 border-t border-border flex items-center ${className}`} {...props}>
    {children}
  </div>
);
