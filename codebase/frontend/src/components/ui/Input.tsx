import React from "react";

interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
}

export const Input: React.FC<InputProps> = ({ 
  label, 
  error, 
  className = "", 
  ...props 
}) => {
  return (
    <div className="flex flex-col gap-1 w-full">
      {label && (
        <label className="text-xs font-medium text-text-secondary">
          {label}
        </label>
      )}
      <input
        className={`bg-background border rounded-[6px] h-[40px] px-3 text-sm text-text-primary outline-none transition-colors focus:border-primary focus:ring-1 focus:ring-primary disabled:bg-surface disabled:text-text-tertiary ${
          error ? "border-error focus:border-error focus:ring-error" : "border-border hover:border-text-tertiary"
        } ${className}`}
        {...props}
      />
      {error && (
        <span className="text-xs text-error">{error}</span>
      )}
    </div>
  );
};
